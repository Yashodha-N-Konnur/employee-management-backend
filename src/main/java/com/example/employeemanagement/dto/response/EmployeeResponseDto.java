package com.example.employeemanagement.dto.response;

import com.example.employeemanagement.entity.EmployeeStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Outbound DTO for Employee.
 * Contains flattened department/role info to prevent N+1 and circular refs.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponseDto {
    private Long           id;
    private String         firstName;
    private String         lastName;
    private String         fullName;       // Computed: firstName + " " + lastName
    private String         email;
    private String         phone;
    private BigDecimal     salary;
    private LocalDate      joiningDate;
    private EmployeeStatus status;

    // Flattened Department info
    private Long           departmentId;
    private String         departmentName;
    private String         departmentCode;

    // Flattened Role info
    private Long           roleId;
    private String         roleName;

    private LocalDateTime  createdAt;
    private LocalDateTime  updatedAt;
}
