package com.example.employeemanagement.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 / Swagger Configuration
 */
@Configuration
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Enter JWT token obtained from POST /api/v1/auth/login"
)
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management System API")
                        .version("1.0.0")
                        .description("""
                                
                                RESTful API for managing Employees, Departments, and Roles.
                                
                                Authentication Flow:
                                1. Use POST /api/v1/auth/login
                                2. Copy accessToken from response
                                3. Click Authorize button
                                4. Enter: Bearer <your_token>
                                
                                Default Credentials:
                                
                                Admin User:
                                username: admin
                                password: admin123
                                
                                Normal User:
                                username: user
                                password: user123
                                """)
                        .contact(new Contact()
                                .name("Employee Management System")
                                .email("admin@company.com"))
                        .license(new License()
                                .name("MIT License")));
    }
}