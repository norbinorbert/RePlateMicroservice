package edu.bbte.replate.auth.service;

import edu.bbte.replate.auth.model.User;
import edu.bbte.replate.shared.dto.incoming.RegisterDto;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User register(RegisterDto registerDto);

    User findByUsername(String username);

    User findByEmail(String email);

    // Return UserDetails for Spring Security
    @NonNull
    @Override
    UserDetails loadUserByUsername(@NonNull String username);
}
