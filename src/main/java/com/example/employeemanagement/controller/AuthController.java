package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.LoginRequestDto;
import com.example.employeemanagement.dto.response.ApiResponse;
import com.example.employeemanagement.dto.response.JwtResponse;
import com.example.employeemanagement.security.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication controller.
 * POST /api/v1/auth/login → returns JWT token
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Login and token management")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      jwtTokenProvider;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenProvider      jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider      = jwtTokenProvider;
    }

    /**
     * Authenticates user credentials and returns a JWT token.
     *
     * <p>Steps:
     * <ol>
     *   <li>Spring Security authenticates via UserDetailsService</li>
     *   <li>On success, JWT is generated and returned</li>
     * </ol>
     */
    @PostMapping("/login")
    @Operation(summary = "Login and receive JWT token",
               description = "Use admin/admin123 or user/user123 for demo credentials.")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequestDto dto) {

        log.info("Login attempt for user: {}", dto.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        JwtResponse jwtResponse = JwtResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpirationMs())
                .username(userDetails.getUsername())
                .role(role)
                .build();

        log.info("User {} logged in successfully", dto.getUsername());
        return ResponseEntity.ok(ApiResponse.success("Login successful", jwtResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout (client-side token invalidation)")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // Stateless JWT: invalidation is client-side (delete token).
        // For true server-side invalidation, implement a token blacklist (Redis).
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(ApiResponse.success("Logout successful. Please delete your token."));
    }
}
