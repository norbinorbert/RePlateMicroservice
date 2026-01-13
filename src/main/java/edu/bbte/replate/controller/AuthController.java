package edu.bbte.replate.controller;

import edu.bbte.replate.dto.incoming.LoginDto;
import edu.bbte.replate.dto.incoming.RegisterDto;
import edu.bbte.replate.exception.BadRequestException;
import edu.bbte.replate.exception.InternalServerErrorException;
import edu.bbte.replate.model.User;
import edu.bbte.replate.service.JwtService;
import edu.bbte.replate.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Map<String, String>> handleRegister(@RequestBody @Valid RegisterDto registerDto) {
        log.info("Handling POST /auth/register request with username '{}'", registerDto.username());

        Map<String, String> responseBody = new ConcurrentHashMap<>();

        // Equal passwords check
        if (!registerDto.password().equals(registerDto.repeatPassword())) {
            log.warn("Provided passwords do not match for registration.");
            throw new BadRequestException("Provided passwords do not match.");
        }

        // Existing username check
        // Return CREATED regardless to prevent user enumeration
        if (userService.findByUsername(registerDto.username()) != null) {
            log.warn(
                    "A user with the username '{}' is already registered, cannot register new user.",
                    registerDto.username());

            responseBody.put("Message", "User registered successfully.");
            return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
        }

        User savedUser = userService.register(registerDto);
        log.info(
                "Successfully created new user with username '{}', Id: {}",
                savedUser.getUsername(),
                savedUser.getId());

        // Do not send location after user registration
        responseBody.put("Message", "User registered successfully.");
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Map<String, String>> handleLogin(@RequestBody @Valid LoginDto loginDto) {
        log.info("Handling POST /auth/login request with username '{}'", loginDto.username());

        Map<String, String> responseBody = new ConcurrentHashMap<>();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
            );

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authentication);
                responseBody.put("Message", "Login successful.");
                responseBody.put("Token", token);

                log.info("User login is successful.");
                return new ResponseEntity<>(responseBody, HttpStatus.OK);
            } else {
                log.warn("User login failed");
                throw new BadRequestException("Invalid username or password.");
            }
        } catch (AuthenticationException e) {
            log.error("Unexpected error occurred while handling POST /auth/login request: {}", e.getMessage());
            throw new InternalServerErrorException("An internal server error occurred.");
        }
    }
}
