package com.example.employeemanagement.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * Outbound DTO for Role.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleResponseDto {
    private Long          id;
    private String        roleName;
    private String        roleDescription;
    private int           employeeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
