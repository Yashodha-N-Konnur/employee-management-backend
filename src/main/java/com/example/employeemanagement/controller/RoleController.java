package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.request.RoleRequestDto;
import com.example.employeemanagement.dto.response.ApiResponse;
import com.example.employeemanagement.dto.response.RoleResponseDto;
import com.example.employeemanagement.service.RoleService;
import com.example.employeemanagement.util.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
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
 * REST controller for Role CRUD operations.
 */
@RestController
@RequestMapping(AppConstants.ROLES)
@Tag(name = "Role Management", description = "APIs to manage employee roles")
@SecurityRequirement(name = "bearerAuth")
@Slf4j
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new role")
    public ResponseEntity<ApiResponse<RoleResponseDto>> createRole(
            @Valid @RequestBody RoleRequestDto dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(AppConstants.ROLE_CREATED, roleService.createRole(dto)));
    }

    @GetMapping
    @Operation(summary = "Get all roles")
    public ResponseEntity<ApiResponse<List<RoleResponseDto>>> getAllRoles() {
        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.ROLES_FETCHED, roleService.getAllRoles()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID")
    public ResponseEntity<ApiResponse<RoleResponseDto>> getRoleById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.ROLE_FETCHED, roleService.getRoleById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update role by ID")
    public ResponseEntity<ApiResponse<RoleResponseDto>> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody RoleRequestDto dto) {

        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.ROLE_UPDATED, roleService.updateRole(id, dto)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete role by ID")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.ok(ApiResponse.success(AppConstants.ROLE_DELETED));
    }

    @GetMapping("/search")
    @Operation(summary = "Search roles by name")
    public ResponseEntity<ApiResponse<List<RoleResponseDto>>> searchRoles(
            @RequestParam String name) {

        return ResponseEntity.ok(
                ApiResponse.success(AppConstants.ROLES_FETCHED, roleService.searchRoles(name)));
    }
}
