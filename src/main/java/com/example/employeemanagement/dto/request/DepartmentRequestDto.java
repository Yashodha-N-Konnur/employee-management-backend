package com.example.employeemanagement.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * Inbound DTO for creating/updating a Department.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequestDto {

    @NotBlank(message = "Department name is required")
    @Size(min = 2, max = 100, message = "Department name must be between 2 and 100 characters")
    private String departmentName;

    @NotBlank(message = "Department code is required")
    @Size(min = 2, max = 20, message = "Department code must be between 2 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Department code must be uppercase letters, digits or underscores")
    private String departmentCode;
}
