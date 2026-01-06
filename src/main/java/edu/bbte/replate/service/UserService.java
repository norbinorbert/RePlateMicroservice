package edu.bbte.replate.service;

import edu.bbte.replate.dto.incoming.RegisterDto;
import edu.bbte.replate.model.User;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User register(RegisterDto registerDto);

    @SuppressWarnings("unused")
    User findById(Long id);

    User findByUsername(String username);

    // Return UserDetails for Spring Security
    @NonNull
    @Override
    UserDetails loadUserByUsername(@NonNull String username);
}
