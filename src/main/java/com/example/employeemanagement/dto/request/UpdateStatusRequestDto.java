package com.example.employeemanagement.dto.request;

import com.example.employeemanagement.entity.EmployeeStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO for PATCH /employees/{id}/status endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusRequestDto {

    @NotNull(message = "Status is required")
    private EmployeeStatus status;
}
