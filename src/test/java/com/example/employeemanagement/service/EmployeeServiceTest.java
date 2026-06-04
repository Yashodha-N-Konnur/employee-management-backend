package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.request.EmployeeRequestDto;
import com.example.employeemanagement.dto.request.UpdateStatusRequestDto;
import com.example.employeemanagement.dto.response.EmployeeResponseDto;
import com.example.employeemanagement.dto.response.PagedResponse;
import com.example.employeemanagement.entity.*;
import com.example.employeemanagement.exception.DuplicateResourceException;
import com.example.employeemanagement.exception.ResourceNotFoundException;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.repository.DepartmentRepository;
import com.example.employeemanagement.repository.EmployeeRepository;
import com.example.employeemanagement.repository.RoleRepository;
import com.example.employeemanagement.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link EmployeeServiceImpl}.
 *
 * Uses Mockito to isolate business logic from infrastructure (DB, mappers).
 * AssertJ provides fluent, readable assertions.
 *
 * Test structure follows AAA: Arrange → Act → Assert.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("EmployeeServiceImpl Unit Tests")
class EmployeeServiceTest {

    @Mock private EmployeeRepository   employeeRepository;
    @Mock private DepartmentRepository departmentRepository;
    @Mock private RoleRepository       roleRepository;
    @Mock private EmployeeMapper       employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    // ── Shared test fixtures ───────────────────────────────────────────────
    private Department        testDepartment;
    private Role              testRole;
    private Employee          testEmployee;
    private EmployeeRequestDto requestDto;
    private EmployeeResponseDto responseDto;

    @BeforeEach
    void setUp() {
        testDepartment = Department.builder()
                .id(1L).departmentName("Engineering").departmentCode("ENG").build();

        testRole = Role.builder()
                .id(1L).roleName("Software Engineer").roleDescription("Develops software").build();

        testEmployee = Employee.builder()
                .id(1L)
                .firstName("John").lastName("Doe")
                .email("john.doe@test.com")
                .phone("9876543210")
                .salary(new BigDecimal("80000.00"))
                .joiningDate(LocalDate.of(2023, 1, 15))
                .status(EmployeeStatus.ACTIVE)
                .department(testDepartment)
                .role(testRole)
                .isDeleted(false)
                .build();

        requestDto = EmployeeRequestDto.builder()
                .firstName("John").lastName("Doe")
                .email("john.doe@test.com")
                .phone("9876543210")
                .salary(new BigDecimal("80000.00"))
                .joiningDate(LocalDate.of(2023, 1, 15))
                .departmentId(1L).roleId(1L)
                .build();

        responseDto = EmployeeResponseDto.builder()
                .id(1L)
                .firstName("John").lastName("Doe")
                .fullName("John Doe")
                .email("john.doe@test.com")
                .status(EmployeeStatus.ACTIVE)
                .departmentId(1L).departmentName("Engineering")
                .roleId(1L).roleName("Software Engineer")
                .build();
    }

    // ══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("createEmployee()")
    class CreateEmployee {

        @Test
        @DisplayName("Should create employee successfully when email is unique")
        void createEmployee_Success() {
            // Arrange
            when(employeeRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
            when(employeeMapper.toEntity(requestDto)).thenReturn(testEmployee);
            when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
            when(employeeMapper.toResponseDto(testEmployee)).thenReturn(responseDto);

            // Act
            EmployeeResponseDto result = employeeService.createEmployee(requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getEmail()).isEqualTo("john.doe@test.com");
            assertThat(result.getFullName()).isEqualTo("John Doe");
            verify(employeeRepository).save(any(Employee.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when email already exists")
        void createEmployee_DuplicateEmail_ThrowsException() {
            // Arrange
            when(employeeRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

            // Act & Assert
            assertThatThrownBy(() -> employeeService.createEmployee(requestDto))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessageContaining("john.doe@test.com");

            verify(employeeRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when department not found")
        void createEmployee_DepartmentNotFound_ThrowsException() {
            // Arrange
            when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
            when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> employeeService.createEmployee(requestDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Department");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when role not found")
        void createEmployee_RoleNotFound_ThrowsException() {
            when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(roleRepository.findById(1L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.createEmployee(requestDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Role");
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getEmployeeById()")
    class GetEmployeeById {

        @Test
        @DisplayName("Should return employee when found")
        void getEmployeeById_Found() {
            when(employeeRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeMapper.toResponseDto(testEmployee)).thenReturn(responseDto);

            EmployeeResponseDto result = employeeService.getEmployeeById(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when not found")
        void getEmployeeById_NotFound() {
            when(employeeRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.getEmployeeById(99L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("getAllEmployees()")
    class GetAllEmployees {

        @Test
        @DisplayName("Should return paginated employees")
        void getAllEmployees_ReturnsPaged() {
            Page<Employee> page = new PageImpl<>(
                    List.of(testEmployee), PageRequest.of(0, 10), 1);
            when(employeeRepository.findAll(any(Pageable.class))).thenReturn(page);
            when(employeeMapper.toResponseDto(testEmployee)).thenReturn(responseDto);

            PagedResponse<EmployeeResponseDto> result =
                    employeeService.getAllEmployees(0, 10, "id", "asc");

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.isFirst()).isTrue();
            assertThat(result.isLast()).isTrue();
        }

        @Test
        @DisplayName("Should return empty paged response when no employees")
        void getAllEmployees_EmptyPage() {
            Page<Employee> emptyPage = new PageImpl<>(Collections.emptyList());
            when(employeeRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            PagedResponse<EmployeeResponseDto> result =
                    employeeService.getAllEmployees(0, 10, "id", "asc");

            assertThat(result.getContent()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("updateEmployee()")
    class UpdateEmployee {

        @Test
        @DisplayName("Should update employee successfully")
        void updateEmployee_Success() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);
            when(departmentRepository.findById(1L)).thenReturn(Optional.of(testDepartment));
            when(roleRepository.findById(1L)).thenReturn(Optional.of(testRole));
            when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
            when(employeeMapper.toResponseDto(testEmployee)).thenReturn(responseDto);

            EmployeeResponseDto result = employeeService.updateEmployee(1L, requestDto);

            assertThat(result).isNotNull();
            verify(employeeRepository).save(any(Employee.class));
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("deleteEmployee()")
    class DeleteEmployee {

        @Test
        @DisplayName("Should soft-delete employee")
        void deleteEmployee_SoftDelete() {
            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

            employeeService.deleteEmployee(1L);

            // Verify soft-delete flags set
            assertThat(testEmployee.getIsDeleted()).isTrue();
            assertThat(testEmployee.getDeletedAt()).isNotNull();
            verify(employeeRepository).save(testEmployee);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException for unknown id")
        void deleteEmployee_NotFound() {
            when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> employeeService.deleteEmployee(999L))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("updateEmployeeStatus()")
    class UpdateStatus {

        @Test
        @DisplayName("Should update status to ON_LEAVE")
        void updateStatus_Success() {
            UpdateStatusRequestDto statusDto =
                    new UpdateStatusRequestDto(EmployeeStatus.ON_LEAVE);

            when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
            when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);
            when(employeeMapper.toResponseDto(testEmployee)).thenReturn(responseDto);

            employeeService.updateEmployeeStatus(1L, statusDto);

            assertThat(testEmployee.getStatus()).isEqualTo(EmployeeStatus.ON_LEAVE);
            verify(employeeRepository).save(testEmployee);
        }
    }

    // ══════════════════════════════════════════════════════════════════════
    @Nested
    @DisplayName("searchByName()")
    class SearchByName {

        @Test
        @DisplayName("Should return employees matching name")
        void searchByName_Found() {
            Page<Employee> page = new PageImpl<>(List.of(testEmployee));
            when(employeeRepository.searchByName(eq("John"), any(Pageable.class))).thenReturn(page);
            when(employeeMapper.toResponseDto(testEmployee)).thenReturn(responseDto);

            PagedResponse<EmployeeResponseDto> result = employeeService.searchByName("John", 0, 10);

            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getFirstName()).isEqualTo("John");
        }
    }
}
