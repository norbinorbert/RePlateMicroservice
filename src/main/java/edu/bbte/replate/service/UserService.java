package edu.bbte.replate.service;

import edu.bbte.replate.model.User;

public interface UserService {
    User register(User user);

    User findById(Long id);

    User findByUsername(String username);
}
