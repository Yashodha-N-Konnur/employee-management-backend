package com.example.employeemanagement.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Standardised error payload returned by the global exception handler.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private int           statusCode;
    private String        error;
    private String        message;
    private String        path;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private List<String>  details;   // Populated for validation errors
}
