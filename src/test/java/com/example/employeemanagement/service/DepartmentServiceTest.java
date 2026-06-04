package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.request.DepartmentRequestDto;
import com.example.employeemanagement.dto.response.DepartmentResponseDto;
import com.example.employeemanagement.entity.Department;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.mapper.DepartmentMapper;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DepartmentServiceImpl Unit Tests")
class DepartmentServiceTest {

    @Mock private DepartmentRepository departmentRepository;
    @Mock private DepartmentMapper     departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department            testDept;
    private DepartmentRequestDto  requestDto;
    private DepartmentResponseDto responseDto;

    @BeforeEach
    void setUp() {
        testDept = Department.builder()
                .id(1L).departmentName("HR").departmentCode("HR001")
                .employees(new ArrayList<>()).build();

        requestDto  = new DepartmentRequestDto("HR", "HR001");
        responseDto = DepartmentResponseDto.builder()
                .id(1L).departmentName("HR").departmentCode("HR001").employeeCount(0).build();
    }

    @Test
    @DisplayName("createDepartment – success when code is unique")
    void createDepartment_Success() {
        when(departmentRepository.existsByDepartmentCode("HR001")).thenReturn(false);
        when(departmentMapper.toEntity(requestDto)).thenReturn(testDept);
        when(departmentRepository.save(testDept)).thenReturn(testDept);
        when(departmentMapper.toResponseDto(testDept)).thenReturn(responseDto);

        DepartmentResponseDto result = departmentService.createDepartment(requestDto);

        assertThat(result.getDepartmentCode()).isEqualTo("HR001");
        verify(departmentRepository).save(any());
    }

    @Test
    @DisplayName("createDepartment – throws DuplicateResourceException for duplicate code")
    void createDepartment_DuplicateCode() {
        when(departmentRepository.existsByDepartmentCode("HR001")).thenReturn(true);

        assertThatThrownBy(() -> departmentService.createDepartment(requestDto))
                .isInstanceOf(DuplicateResourceException.class);

        verify(departmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("getDepartmentById – throws ResourceNotFoundException for unknown id")
    void getDepartmentById_NotFound() {
        when(departmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getDepartmentById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("getAllDepartments – returns all departments")
    void getAllDepartments_ReturnsList() {
        when(departmentRepository.findAll()).thenReturn(List.of(testDept));
        when(departmentMapper.toResponseDto(testDept)).thenReturn(responseDto);

        List<DepartmentResponseDto> result = departmentService.getAllDepartments();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDepartmentCode()).isEqualTo("HR001");
    }

    @Test
    @DisplayName("deleteDepartment – deletes when found")
    void deleteDepartment_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDept));

        departmentService.deleteDepartment(1L);

        verify(departmentRepository).delete(testDept);
    }
}
