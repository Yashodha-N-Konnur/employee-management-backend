package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.DepartmentRequestDto;
import com.example.employeemanagement.dto.response.ApiResponse;
import com.example.employeemanagement.dto.response.DepartmentResponseDto;
import com.example.employeemanagement.service.DepartmentService;
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

import java.util.List;

/**
 * REST controller for Department CRUD operations.
 * API versioned under /api/v1/departments.
 */
@RestController
@RequestMapping(AppConstants.DEPARTMENTS)
@Tag(name = "Department Management", description = "APIs to manage departments")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // ── POST /api/v1/departments ───────────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new department")
    public ResponseEntity<ApiResponse<DepartmentResponseDto>> createDepartment(
            @Valid @RequestBody DepartmentRequestDto dto) {

        log.info("POST /departments - {}", dto.getDepartmentCode());
        DepartmentResponseDto created = departmentService.createDepartment(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.DEPT_CREATED, created));
    }

    // ── GET /api/v1/departments ────────────────────────────────────────────
    @GetMapping
    @Operation(summary = "Get all departments")
    public ResponseEntity<ApiResponse<List<DepartmentResponseDto>>> getAllDepartments() {
        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.DEPTS_FETCHED, departmentService.getAllDepartments()));
    }

    // ── GET /api/v1/departments/{id} ───────────────────────────────────────
    @GetMapping("/{id}")
    @Operation(summary = "Get department by ID")
    public ResponseEntity<ApiResponse<DepartmentResponseDto>> getDepartmentById(
            @Parameter(description = "Department ID") @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.DEPT_FETCHED, departmentService.getDepartmentById(id)));
    }

    // ── PUT /api/v1/departments/{id} ───────────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update department by ID")
    public ResponseEntity<ApiResponse<DepartmentResponseDto>> updateDepartment(
            @PathVariable Long id,
            @Valid @RequestBody DepartmentRequestDto dto) {

        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.DEPT_UPDATED, departmentService.updateDepartment(id, dto)));
    }

    // ── DELETE /api/v1/departments/{id} ───────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete department by ID")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.DEPT_DELETED));
    }

    // ── GET /api/v1/departments/search?name=Eng ───────────────────────────
    @GetMapping("/search")
    @Operation(summary = "Search departments by name")
    public ResponseEntity<ApiResponse<List<DepartmentResponseDto>>> searchDepartments(
            @RequestParam String name) {

        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.DEPTS_FETCHED, departmentService.searchDepartments(name)));
    }
}
