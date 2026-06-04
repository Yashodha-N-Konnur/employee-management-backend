package com.example.employeemanagement.service.impl;

import com.example.employeemanagement.dto.request.RoleRequestDto;
import com.example.employeemanagement.dto.response.RoleResponseDto;
import com.example.employeemanagement.entity.Role;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.mapper.RoleMapper;
import com.example.employeemanagement.repository.RoleRepository;
import com.example.employeemanagement.service.RoleService;
import com.example.employeemanagement.util.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic implementation for Role management.
 */
@Service
@Slf4j
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper     roleMapper;

    public RoleServiceImpl(RoleRepository roleRepository, RoleMapper roleMapper) {
        this.roleRepository = roleRepository;
        this.roleMapper     = roleMapper;
    }

    @Override
    @Transactional
    public RoleResponseDto createRole(RoleRequestDto dto) {
        log.info("Creating role: {}", dto.getRoleName());

        if (roleRepository.existsByRoleName(dto.getRoleName())) {
            throw new DuplicateResourceException(AppConstants.ROLE_NAME_EXISTS + dto.getRoleName());
        }

        Role saved = roleRepository.save(roleMapper.toEntity(dto));
        log.info("Role created with id: {}", saved.getId());
        return buildResponseWithCount(saved);
    }

    @Override
    public RoleResponseDto getRoleById(Long id) {
        return buildResponseWithCount(findRoleById(id));
    }

    @Override
    public List<RoleResponseDto> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(this::buildResponseWithCount)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoleResponseDto> searchRoles(String name) {
        return roleRepository.findByRoleNameContainingIgnoreCase(name)
                .stream()
                .map(this::buildResponseWithCount)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleResponseDto updateRole(Long id, RoleRequestDto dto) {
        log.info("Updating role id: {}", id);
        Role role = findRoleById(id);

        if (roleRepository.existsByRoleNameAndIdNot(dto.getRoleName(), id)) {
            throw new DuplicateResourceException(AppConstants.ROLE_NAME_EXISTS + dto.getRoleName());
        }

        roleMapper.updateEntityFromDto(dto, role);
        return buildResponseWithCount(roleRepository.save(role));
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        log.info("Deleting role id: {}", id);
        roleRepository.delete(findRoleById(id));
    }

    private Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
    }

    private RoleResponseDto buildResponseWithCount(Role role) {
        RoleResponseDto dto = roleMapper.toResponseDto(role);
        dto.setEmployeeCount(role.getEmployees().size());
        return dto;
    }
}
