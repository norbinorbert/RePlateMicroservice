package edu.bbte.replate.service.impl;

import edu.bbte.replate.dto.incoming.RegisterDto;
import edu.bbte.replate.mapper.UserMapper;
import edu.bbte.replate.model.User;
import edu.bbte.replate.model.UserDetailsImpl;
import edu.bbte.replate.model.UserRole;
import edu.bbte.replate.repository.UserRepository;
import edu.bbte.replate.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.getRoles().add(UserRole.ROLE_USER);
        return userRepository.saveAndFlush(user);
    }

    @Override
    public User findById(Long id) {
        log.info("Attempting to find user with id: {}", id);
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        log.info("Attempting to find user with username: {}", username);
        return userRepository.findUserByUsername(username);
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
