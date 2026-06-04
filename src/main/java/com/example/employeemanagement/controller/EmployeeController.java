package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.EmployeeRequestDto;
import com.example.employeemanagement.dto.request.UpdateStatusRequestDto;
import com.example.employeemanagement.dto.response.ApiResponse;
import com.example.employeemanagement.dto.response.EmployeeResponseDto;
import com.example.employeemanagement.dto.response.PagedResponse;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Employee management.
 * Exposes versioned endpoints under /api/v1/employees.
 *
 * <p>Role-based access:
 * <ul>
 *   <li>GET  → ROLE_USER or ROLE_ADMIN</li>
 *   <li>POST / PUT / DELETE → ROLE_ADMIN only</li>
 * </ul>
 */
@RestController
@RequestMapping(AppConstants.EMPLOYEES)
@Tag(name = "Employee Management", description = "APIs to manage employees")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // ── POST /api/v1/employees ─────────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new employee",
               description = "Creates a new employee record. Email must be unique.")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> createEmployee(
            @Valid @RequestBody EmployeeRequestDto dto) {

        log.info("POST /employees - creating employee: {}", dto.getEmail());
        EmployeeResponseDto created = employeeService.createEmployee(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.EMPLOYEE_CREATED, created));
    }

    // ── GET /api/v1/employees ──────────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Get all employees (paginated & sorted)")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> getAllEmployees(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {

        PagedResponse<EmployeeResponseDto> employees =
                employeeService.getAllEmployees(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.EMPLOYEES_FETCHED, employees));
    }

    // ── GET /api/v1/employees/{id} ─────────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> getEmployeeById(
            @Parameter(description = "Employee ID", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.EMPLOYEE_FETCHED, employeeService.getEmployeeById(id)));
    }

    // ── PUT /api/v1/employees/{id} ─────────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update employee by ID")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDto dto) {

        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.EMPLOYEE_UPDATED, employeeService.updateEmployee(id, dto)));
    }

    // ── DELETE /api/v1/employees/{id} ─────────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Soft-delete employee by ID",
               description = "Marks employee as deleted. Record is retained for auditing.")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.EMPLOYEE_DELETED));
    }

    // ── PATCH /api/v1/employees/{id}/status ───────────────────────────────
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update employee status",
               description = "Allowed values: ACTIVE, INACTIVE, ON_LEAVE, TERMINATED")
    public ResponseEntity<ApiResponse<EmployeeResponseDto>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequestDto dto) {

        return ResponseEntity.ok(ApiResponse.success(
                AppConstants.EMPLOYEE_UPDATED, employeeService.updateEmployeeStatus(id, dto)));
    }

    // ── GET /api/v1/employees/search?name=John ─────────────────────────────
    @GetMapping("/search")
    @Operation(summary = "Search employees by name (first or last)")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> searchByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                AppConstants.EMPLOYEES_FETCHED, employeeService.searchByName(name, page, size)));
    }

    // ── GET /api/v1/employees/department/{departmentId} ───────────────────
    @GetMapping("/department/{departmentId}")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> getByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                AppConstants.EMPLOYEES_FETCHED,
                employeeService.getEmployeesByDepartment(departmentId, page, size)));
    }

    // ── GET /api/v1/employees/role/{roleId} ───────────────────────────────
    @GetMapping("/role/{roleId}")
    @Operation(summary = "Filter employees by role")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> getByRole(
            @PathVariable Long roleId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                AppConstants.EMPLOYEES_FETCHED,
                employeeService.getEmployeesByRole(roleId, page, size)));
    }

    // ── GET /api/v1/employees/status/{status} ─────────────────────────────
    @GetMapping("/status/{status}")
    @Operation(summary = "Filter employees by status")
    public ResponseEntity<ApiResponse<PagedResponse<EmployeeResponseDto>>> getByStatus(
            @PathVariable EmployeeStatus status,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(ApiResponse.success(
                AppConstants.EMPLOYEES_FETCHED,
                employeeService.getEmployeesByStatus(status, page, size)));
    }
}
