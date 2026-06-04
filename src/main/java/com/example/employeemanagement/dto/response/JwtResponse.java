package com.example.employeemanagement.dto.response;

import lombok.*;

/**
 * Response payload returned after a successful login.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String  accessToken;
    private String  tokenType;
    private long    expiresIn;
    private String  username;
    private String  role;
}
