package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.request.EmployeeRequestDto;
import com.example.employeemanagement.dto.request.UpdateStatusRequestDto;
import com.example.employeemanagement.dto.response.EmployeeResponseDto;
import com.example.employeemanagement.dto.response.PagedResponse;
import com.example.employeemanagement.entity.EmployeeStatus;

/**
 * Service contract for Employee operations.
 * All methods return DTOs – the service layer is responsible for mapping.
 */
public interface EmployeeService {

    EmployeeResponseDto createEmployee(EmployeeRequestDto dto);

    EmployeeResponseDto getEmployeeById(Long id);

    PagedResponse<EmployeeResponseDto> getAllEmployees(int page, int size, String sortBy, String sortDir);

    EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto);

    void deleteEmployee(Long id);   // Soft delete

    EmployeeResponseDto updateEmployeeStatus(Long id, UpdateStatusRequestDto dto);

    // ── Search & Filter ────────────────────────────────────────────────────
    PagedResponse<EmployeeResponseDto> searchByName(String name, int page, int size);

    PagedResponse<EmployeeResponseDto> getEmployeesByDepartment(Long departmentId, int page, int size);

    PagedResponse<EmployeeResponseDto> getEmployeesByRole(Long roleId, int page, int size);

    PagedResponse<EmployeeResponseDto> getEmployeesByStatus(EmployeeStatus status, int page, int size);
}
