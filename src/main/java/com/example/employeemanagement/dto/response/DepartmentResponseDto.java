package com.example.employeemanagement.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Outbound DTO for Department – avoids circular JSON via the employees list.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentResponseDto {
    private Long          id;
    private String        departmentName;
    private String        departmentCode;
    private int           employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
