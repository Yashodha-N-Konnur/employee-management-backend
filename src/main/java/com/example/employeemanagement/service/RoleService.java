package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.request.RoleRequestDto;
import com.example.employeemanagement.dto.response.RoleResponseDto;

import java.util.List;

/**
 * Service contract for Role operations.
 */
public interface RoleService {

    RoleResponseDto createRole(RoleRequestDto dto);

    RoleResponseDto getRoleById(Long id);

    List<RoleResponseDto> getAllRoles();

    RoleResponseDto updateRole(Long id, RoleRequestDto dto);

    void deleteRole(Long id);

    List<RoleResponseDto> searchRoles(String name);
}
