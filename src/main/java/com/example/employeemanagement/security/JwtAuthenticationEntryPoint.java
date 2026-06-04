package com.example.employeemanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles authentication failures (missing/invalid token).
 * Returns a standardised JSON 401 response instead of the default Spring redirect.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest      request,
                         HttpServletResponse     response,
                         AuthenticationException authException) throws IOException {

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, Object> body = new HashMap<>();
        body.put("statusCode", HttpStatus.UNAUTHORIZED.value());
        body.put("error",      "Unauthorized");
        body.put("message",    "Access denied. Please provide a valid JWT token.");
        body.put("path",       request.getRequestURI());
        body.put("timestamp",  LocalDateTime.now().toString());

        objectMapper.writeValue(response.getOutputStream(), body);
    }
}
