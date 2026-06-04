package com.example.employeemanagement.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 / Swagger configuration.
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 */
@Configuration
@SecurityScheme(
    name   = "bearerAuth",
    type   = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description  = "Enter JWT token obtained from POST /api/v1/auth/login"
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
                            
                            **Authentication:** Use POST /api/v1/auth/login to get a JWT token,
                            then click 'Authorize' and enter: Bearer {your_token}
                            
                            **Default credentials:**
                            - Admin: admin / admin123 (full access)
                            - User: user / user123 (read-only access)
                            """)
                        .contact(new Contact()
                                .name("Employee Management System")
                                .email("admin@company.com"))
                        .license(new License().name("MIT License")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development"),
                        new Server().url("https://api.company.com").description("Production")));
    }
}
