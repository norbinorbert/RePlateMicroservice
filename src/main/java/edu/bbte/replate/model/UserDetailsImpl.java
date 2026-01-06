package edu.bbte.replate.model;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    // A SimpleGrantedAuthority needs a String representation of each role
    public static final String ADMIN_AUTHORITY_NAME = "ADMIN";
    public static final String USER_AUTHORITY_NAME = "USER";

    private final Long id;

    private final String username;

    private final String password;

    private final Collection<SimpleGrantedAuthority> grantedAuthorities;

    public UserDetailsImpl(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();

        this.grantedAuthorities = new ArrayList<>();
        // If the User entity has admin role, add it
        if (user.getRoles().contains(UserRole.ROLE_ADMIN)) {
            SimpleGrantedAuthority simpleGrantedAuthority =
                    new SimpleGrantedAuthority(ADMIN_AUTHORITY_NAME);
            grantedAuthorities.add(simpleGrantedAuthority);
        }

        // Always add regular user role
        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(USER_AUTHORITY_NAME);
        grantedAuthorities.add(simpleGrantedAuthority);
    }

    @Override
    @NonNull
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.copyOf(grantedAuthorities);
    }

    @Override
    @NonNull
    public String getPassword() {
        return password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return username;
    }

    @NonNull
    public Long getId() {
        return id;
    }
}
