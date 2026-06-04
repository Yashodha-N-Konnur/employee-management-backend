package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.request.EmployeeRequestDto;
import com.example.employeemanagement.dto.request.UpdateStatusRequestDto;
import com.example.employeemanagement.dto.response.EmployeeResponseDto;
import com.example.employeemanagement.dto.response.PagedResponse;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.RoleRepository;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic implementation for Employee management.
 *
 * Key patterns used:
 * - Constructor injection (no @Autowired)
 * - @Transactional(readOnly=true) at class level; @Transactional overrides for writes
 * - Optional.orElseThrow() for safe entity lookup
 * - MapStruct for object mapping
 * - PagedResponse for standardised paginated output
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository   employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleRepository       roleRepository;
    private final EmployeeMapper       employeeMapper;

    public EmployeeServiceImpl(EmployeeRepository   employeeRepository,
                                DepartmentRepository departmentRepository,
                                RoleRepository       roleRepository,
                                EmployeeMapper       employeeMapper) {
        this.employeeRepository   = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.roleRepository       = roleRepository;
        this.employeeMapper       = employeeMapper;
    }

    // ── CREATE ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public EmployeeResponseDto createEmployee(EmployeeRequestDto dto) {
        log.info("Creating employee with email: {}", dto.getEmail());

        // Business rule: email must be unique across all employees
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(AppConstants.EMAIL_ALREADY_EXISTS + dto.getEmail());
        }

        // Resolve relationships – fail fast if FK is invalid
        Department department = findDepartmentById(dto.getDepartmentId());
        Role       role       = findRoleById(dto.getRoleId());

        Employee employee = employeeMapper.toEntity(dto);
        employee.setDepartment(department);
        employee.setRole(role);

        if (employee.getStatus() == null) {
            employee.setStatus(EmployeeStatus.ACTIVE);
        }

        Employee saved = employeeRepository.save(employee);
        log.info("Employee created with id: {}", saved.getId());
        return employeeMapper.toResponseDto(saved);
    }

    // ── READ ───────────────────────────────────────────────────────────────

    @Override
    public EmployeeResponseDto getEmployeeById(Long id) {
        log.debug("Fetching employee with id: {}", id);
        Employee employee = employeeRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return employeeMapper.toResponseDto(employee);
    }

    @Override
    public PagedResponse<EmployeeResponseDto> getAllEmployees(int page, int size,
                                                              String sortBy, String sortDir) {
        log.debug("Fetching all employees - page:{} size:{} sort:{} {}", page, size, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Page<Employee> employeePage = employeeRepository.findAll(PageRequest.of(page, size, sort));
        return buildPagedResponse(employeePage);
    }

    // ── UPDATE ─────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployee(Long id, EmployeeRequestDto dto) {
        log.info("Updating employee id: {}", id);
        Employee employee = findEmployeeById(id);

        // Email uniqueness check (excluding current employee)
        if (employeeRepository.existsByEmailAndIdNot(dto.getEmail(), id)) {
            throw new DuplicateResourceException(AppConstants.EMAIL_ALREADY_EXISTS + dto.getEmail());
        }

        // Resolve FK relationships
        Department department = findDepartmentById(dto.getDepartmentId());
        Role       role       = findRoleById(dto.getRoleId());

        employeeMapper.updateEntityFromDto(dto, employee);
        employee.setDepartment(department);
        employee.setRole(role);

        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated: {}", updated.getId());
        return employeeMapper.toResponseDto(updated);
    }

    // ── DELETE (Soft) ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public void deleteEmployee(Long id) {
        log.info("Soft-deleting employee id: {}", id);
        Employee employee = findEmployeeById(id);
        employee.softDelete();             // Sets isDeleted=true, deletedAt=now
        employeeRepository.save(employee); // Persist the soft-delete flags
        log.info("Employee soft-deleted: {}", id);
    }

    // ── STATUS UPDATE ──────────────────────────────────────────────────────

    @Override
    @Transactional
    public EmployeeResponseDto updateEmployeeStatus(Long id, UpdateStatusRequestDto dto) {
        log.info("Updating status of employee {} to {}", id, dto.getStatus());
        Employee employee = findEmployeeById(id);
        employee.setStatus(dto.getStatus());
        return employeeMapper.toResponseDto(employeeRepository.save(employee));
    }

    // ── SEARCH & FILTER ────────────────────────────────────────────────────

    @Override
    public PagedResponse<EmployeeResponseDto> searchByName(String name, int page, int size) {
        log.debug("Searching employees by name: {}", name);
        Pageable pageable = PageRequest.of(page, size, Sort.by("firstName").ascending());
        return buildPagedResponse(employeeRepository.searchByName(name, pageable));
    }

    @Override
    public PagedResponse<EmployeeResponseDto> getEmployeesByDepartment(Long departmentId, int page, int size) {
        log.debug("Fetching employees for department: {}", departmentId);
        // Verify department exists before querying employees
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department", "id", departmentId);
        }
        Pageable pageable = PageRequest.of(page, size);
        return buildPagedResponse(employeeRepository.findByDepartmentId(departmentId, pageable));
    }

    @Override
    public PagedResponse<EmployeeResponseDto> getEmployeesByRole(Long roleId, int page, int size) {
        log.debug("Fetching employees for role: {}", roleId);
        if (!roleRepository.existsById(roleId)) {
            throw new ResourceNotFoundException("Role", "id", roleId);
        }
        Pageable pageable = PageRequest.of(page, size);
        return buildPagedResponse(employeeRepository.findByRoleId(roleId, pageable));
    }

    @Override
    public PagedResponse<EmployeeResponseDto> getEmployeesByStatus(EmployeeStatus status, int page, int size) {
        log.debug("Fetching employees with status: {}", status);
        Pageable pageable = PageRequest.of(page, size);
        return buildPagedResponse(employeeRepository.findByStatus(status, pageable));
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
    }

    private Department findDepartmentById(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }

    private Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    /**
     * Converts a {@link Page} of Employee entities to a standardised {@link PagedResponse}.
     */
    private PagedResponse<EmployeeResponseDto> buildPagedResponse(Page<Employee> page) {
        List<EmployeeResponseDto> content = page.getContent()
                .stream()
                .map(employeeMapper::toResponseDto)
                .collect(Collectors.toList());

        return PagedResponse.<EmployeeResponseDto>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .empty(page.isEmpty())
                .build();
    }
}
