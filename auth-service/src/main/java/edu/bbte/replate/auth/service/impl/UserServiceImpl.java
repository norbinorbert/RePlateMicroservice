package edu.bbte.replate.auth.service.impl;

import edu.bbte.replate.auth.mapper.UserMapper;import edu.bbte.replate.auth.model.User;import edu.bbte.replate.auth.model.UserRole;import edu.bbte.replate.auth.repository.UserRepository;import edu.bbte.replate.auth.service.UserService;import edu.bbte.replate.auth.util.UserDetailsImpl;import edu.bbte.replate.shared.dto.incoming.RegisterDto;import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User register(RegisterDto registerDto) {
        log.info("Attempting to register new user with name: {}", registerDto.username());

        User user = userMapper.registerDtoToUser(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.password()));
        user.getRoles().add(UserRole.ROLE_USER);
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User findByUsername(String username) {
        log.info("Attempting to find user with username: {}", username);
        return userRepository.findUserByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        log.info("Attempting to find user with email: {}", email);
        return userRepository.findUserByEmail(email);
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username);
        if (user == null) {
            String message = String.format("No user with username '%s' was found", username);
            throw new UsernameNotFoundException(message);
        }
        return new UserDetailsImpl(user);
    }
}
