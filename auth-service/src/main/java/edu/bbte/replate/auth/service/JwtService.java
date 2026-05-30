package edu.bbte.replate.auth.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(Authentication authentication);

    String extractUsername(String token);

    boolean validateToken(String token, UserDetails userDetails);
}
