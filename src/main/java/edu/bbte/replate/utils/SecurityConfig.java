package edu.bbte.replate.utils;

import edu.bbte.replate.filter.JwtAuthFilter;
import edu.bbte.replate.model.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/auth/**").permitAll()

                        // Public GET access
                        .requestMatchers(HttpMethod.GET, "/listings/**").permitAll()

                        // Restrict write access to authenticated users
                        .requestMatchers(HttpMethod.POST, "/listings/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/listings/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/listings/**").authenticated()

                        // Admin endpoints
                        .requestMatchers("/admin/**").hasRole(UserDetailsImpl.ADMIN_AUTHORITY_NAME)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
