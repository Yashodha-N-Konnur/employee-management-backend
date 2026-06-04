package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.request.DepartmentRequestDto;
import com.example.employeemanagement.dto.response.DepartmentResponseDto;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.mapper.DepartmentMapper;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.service.DepartmentService;
import com.example.employeemanagement.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic implementation for Department management.
 *
 * <p>Follows SOLID:
 * <ul>
 *   <li>SRP – only Department business logic</li>
 *   <li>OCP – behaviour extended via interface, not modification</li>
 *   <li>DIP – depends on abstractions (repository interface, mapper interface)</li>
 * </ul>
 */
@Service
@Slf4j
@Transactional(readOnly = true)          // Default read-only; write methods override
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper     departmentMapper;

    /** Constructor injection (no @Autowired required; Spring infers single constructor). */
    public DepartmentServiceImpl(DepartmentRepository departmentRepository,
                                  DepartmentMapper     departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper     = departmentMapper;
    }

    // ── CREATE ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DepartmentResponseDto createDepartment(DepartmentRequestDto dto) {
        log.info("Creating department with code: {}", dto.getDepartmentCode());

        if (departmentRepository.existsByDepartmentCode(dto.getDepartmentCode())) {
            throw new DuplicateResourceException(AppConstants.DEPT_CODE_EXISTS + dto.getDepartmentCode());
        }

        Department department = departmentMapper.toEntity(dto);
        Department saved      = departmentRepository.save(department);

        log.info("Department created with id: {}", saved.getId());
        return buildResponseWithCount(saved);
    }

    // ── READ ───────────────────────────────────────────────────────────────

    @Override
    public DepartmentResponseDto getDepartmentById(Long id) {
        log.debug("Fetching department with id: {}", id);
        Department department = findDepartmentById(id);
        return buildResponseWithCount(department);
    }

    @Override
    public List<DepartmentResponseDto> getAllDepartments() {
        log.debug("Fetching all departments");
        return departmentRepository.findAll()
                .stream()
                .map(this::buildResponseWithCount)
                .collect(Collectors.toList());
    }

    @Override
    public List<DepartmentResponseDto> searchDepartments(String name) {
        log.debug("Searching departments by name: {}", name);
        return departmentRepository.findByDepartmentNameContainingIgnoreCase(name)
                .stream()
                .map(this::buildResponseWithCount)
                .collect(Collectors.toList());
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public DepartmentResponseDto updateDepartment(Long id, DepartmentRequestDto dto) {
        log.info("Updating department id: {}", id);

        Department department = findDepartmentById(id);

        if (departmentRepository.existsByDepartmentCodeAndIdNot(dto.getDepartmentCode(), id)) {
            throw new DuplicateResourceException(AppConstants.DEPT_CODE_EXISTS + dto.getDepartmentCode());
        }

        departmentMapper.updateEntityFromDto(dto, department);
        Department updated = departmentRepository.save(department);

        log.info("Department updated: {}", updated.getId());
        return buildResponseWithCount(updated);
    }

    // ── DELETE ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Deleting department id: {}", id);
        Department department = findDepartmentById(id);
        departmentRepository.delete(department);
        log.info("Department deleted: {}", id);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    /** Enriches the DTO with the current employee count. */
    private DepartmentResponseDto buildResponseWithCount(Department department) {
        DepartmentResponseDto dto = departmentMapper.toResponseDto(department);
        dto.setEmployeeCount(department.getEmployees().size());
        return dto;
    }
}
