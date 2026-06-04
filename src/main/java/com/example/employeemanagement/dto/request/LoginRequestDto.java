package com.example.employeemanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Inbound DTO for authentication (login).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDto {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
