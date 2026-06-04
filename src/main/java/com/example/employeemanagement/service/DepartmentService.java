package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.request.DepartmentRequestDto;
import com.example.employeemanagement.dto.response.DepartmentResponseDto;

import java.util.List;

/**
 * Service contract for Department operations.
 * Defines the business API consumed by controllers.
 */
public interface DepartmentService {

    DepartmentResponseDto createDepartment(DepartmentRequestDto dto);

    DepartmentResponseDto getDepartmentById(Long id);

    List<DepartmentResponseDto> getAllDepartments();

    DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto dto);

    void deleteDepartment(Long id);

    List<DepartmentResponseDto> searchDepartments(String name);
}
