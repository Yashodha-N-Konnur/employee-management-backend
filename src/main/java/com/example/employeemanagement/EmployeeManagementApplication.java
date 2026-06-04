package com.example.employeemanagement;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the Employee Management System.
 *
 * <p>Enables:
 * <ul>
 *   <li>Spring Boot auto-configuration</li>
 *   <li>JPA Auditing for createdAt / updatedAt fields</li>
 * </ul>
 */
@SpringBootApplication
@EnableJpaAuditing
@OpenAPIDefinition(
    info = @Info(
        title       = "Employee Management System API",
        version     = "1.0.0",
        description = "RESTful API to manage Employees, Departments and Roles"
    )
)
public class EmployeeManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeManagementApplication.class, args);
    }
}
