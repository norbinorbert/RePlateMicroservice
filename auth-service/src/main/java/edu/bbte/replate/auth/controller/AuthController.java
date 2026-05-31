package edu.bbte.replate.auth.controller;

import edu.bbte.replate.auth.model.User;
import edu.bbte.replate.auth.service.JwtService;
import edu.bbte.replate.auth.service.UserService;
import edu.bbte.replate.shared.dto.incoming.LoginDto;
import edu.bbte.replate.shared.dto.incoming.RegisterDto;
import edu.bbte.replate.shared.dto.internal.TokenValidationResponseDto;
import edu.bbte.replate.shared.dto.internal.UserInfoDto;
import edu.bbte.replate.shared.dto.outgoing.LoginResponseDto;
import edu.bbte.replate.shared.dto.outgoing.SimpleMessageResponseDto;
import edu.bbte.replate.shared.exception.BadRequestException;
import edu.bbte.replate.shared.exception.InternalServerErrorException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<SimpleMessageResponseDto> handleRegister(@RequestBody @Valid RegisterDto registerDto) {
        log.info("Handling POST /auth/register request with username '{}'", registerDto.username());

        // Equal passwords check
        if (!registerDto.password().equals(registerDto.repeatPassword())) {
            log.warn("Provided passwords do not match for registration.");
            throw new BadRequestException("Provided passwords do not match.");
        }

        // Existing username check
        if (userService.findByUsername(registerDto.username()) != null) {
            log.warn(
                    "A user with the username '{}' is already registered, cannot register new user.",
                    registerDto.username());

            var responseBody = new SimpleMessageResponseDto("A user with an identical username already exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        // Existing email check
        if (userService.findByEmail(registerDto.email()) != null) {
            log.warn(
                    "A user with the email address '{}' is already registered, cannot register new user.",
                    registerDto.email());
            var responseBody = new SimpleMessageResponseDto("A user with an identical email address already exists.");
            return new ResponseEntity<>(responseBody, HttpStatus.BAD_REQUEST);
        }

        User savedUser = userService.register(registerDto);
        log.info(
                "Successfully created new user with username '{}', Id: {}",
                savedUser.getUsername(),
                savedUser.getId());

        // Do not send location after user registration
        var responseBody = new SimpleMessageResponseDto("User registered successfully.");
        return new ResponseEntity<>(responseBody, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<LoginResponseDto> handleLogin(@RequestBody @Valid LoginDto loginDto) {
        log.info("Handling POST /auth/login request with username '{}'", loginDto.username());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
            );

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authentication);
                log.info("User login is successful.");
                var responseBody = new LoginResponseDto("Login successful.", token,
                        userService.findByUsername(loginDto.username()).getId());
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

    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<UserInfoDto> handleGetUserByUsername(@PathVariable String username) {
        log.info("Handling GET /auth/user/{} request.", username);

        User user = userService.findByUsername(username);
        if (user == null) {
            throw new BadRequestException("User with username '" + username + "' not found.");
        }

        UserInfoDto userInfo = new UserInfoDto(user.getId(), user.getUsername(), user.getEmail());
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/validate-token")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenValidationResponseDto> handleValidateToken(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        log.info("Handling POST /auth/validate-token request.");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header.");
            var response = new TokenValidationResponseDto(null, null, null, false);
            return ResponseEntity.ok(response);
        }

        try {
            String token = authorizationHeader.substring(7);
            String username = jwtService.extractUsername(token);

            if (username == null) {
                log.warn("Could not extract username from token.");
                var response = new TokenValidationResponseDto(null, null, null, false);
                return ResponseEntity.ok(response);
            }

            User user = userService.findByUsername(username);
            if (user == null) {
                log.warn("User '{}' not found in database.", username);
                var response = new TokenValidationResponseDto(null, null, null, false);
                return ResponseEntity.ok(response);
            }

            UserDetails userDetails = userService.loadUserByUsername(username);
            if (!jwtService.validateToken(token, userDetails)) {
                log.warn("Invalid JWT token for user '{}'.", username);
                var response = new TokenValidationResponseDto(null, null, null, false);
                return ResponseEntity.ok(response);
            }

            log.info("Token validated successfully for user '{}'.", username);
            var response = new TokenValidationResponseDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    true
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage(), e);
            var response = new TokenValidationResponseDto(null, null, null, false);
            return ResponseEntity.ok(response);
        }
    }
}
