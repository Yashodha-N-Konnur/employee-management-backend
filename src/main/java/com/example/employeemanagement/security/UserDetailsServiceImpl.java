
package com.example.employeemanagement.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * In-memory UserDetailsService for demo/dev purposes.
 */
@Service
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    @Value("${app.security.admin.username}")
    private String adminUsername;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    @Value("${app.security.user.username}")
    private String userUsername;

    @Value("${app.security.user.password}")
    private String userPassword;

    // Local encoder instance (avoids circular dependency)
    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        log.debug("Loading user by username: {}", username);

        if (username.equals(adminUsername)) {

            return User.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode(adminPassword))
                    .roles("ADMIN", "USER")
                    .build();
        }

        if (username.equals(userUsername)) {

            return User.builder()
                    .username(userUsername)
                    .password(passwordEncoder.encode(userPassword))
                    .roles("USER")
                    .build();
        }

        throw new UsernameNotFoundException(
                "User not found: " + username
        );
    }
}

