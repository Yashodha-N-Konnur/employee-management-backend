package com.example.employeemanagement.dto.request;

import com.example.employeemanagement.entity.EmployeeStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Inbound DTO for creating/updating an Employee.
 * Jakarta Validation annotations enforce field-level constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequestDto {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Phone must be a valid 10-digit Indian mobile number")
    private String phone;

    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than 0")
    @DecimalMax(value = "9999999.99", message = "Salary must not exceed 9,999,999.99")
    @Digits(integer = 7, fraction = 2, message = "Salary format invalid")
    private BigDecimal salary;

    @PastOrPresent(message = "Joining date cannot be in the future")
    private LocalDate joiningDate;

    private EmployeeStatus status;

    @NotNull(message = "Department ID is required")
    @Positive(message = "Department ID must be a positive number")
    private Long departmentId;

    @NotNull(message = "Role ID is required")
    @Positive(message = "Role ID must be a positive number")
    private Long roleId;
}
