package com.example.employeemanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Inbound DTO for creating/updating a Role.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto {

    @NotBlank(message = "Role name is required")
    @Size(min = 2, max = 50, message = "Role name must be between 2 and 50 characters")
    private String roleName;

    @Size(max = 255, message = "Role description must not exceed 255 characters")
    private String roleDescription;
}
