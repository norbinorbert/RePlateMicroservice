package edu.bbte.replate.service.impl;

import edu.bbte.replate.model.User;
import edu.bbte.replate.repository.UserRepository;
import edu.bbte.replate.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User register(User user) {
        log.info("Attempting to register new user with name: {}", user.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
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
}
