package edu.bbte.replate.utils;

import edu.bbte.replate.model.UserDetailsImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("adminSecurity")
public class AdminSecurity {
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(UserDetailsImpl.ADMIN_AUTHORITY_NAME);
            // Safe to assert since JwtAuthFilter treats user as unauthorized if userDetails is invalid/not set
            assert userDetails != null;
            return userDetails.getAuthorities().contains(adminAuthority);
        } else {
            throw new AccessDeniedException("Access to resource is unauthorized.");
        }
    }
}
