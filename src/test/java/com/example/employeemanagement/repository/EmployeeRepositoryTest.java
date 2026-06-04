package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Repository slice tests using an in-memory H2 database.
 *
 * {@code @DataJpaTest} loads only JPA-related beans (repositories, entities, DataSource).
 * No Spring Security or service layer beans are loaded.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("EmployeeRepository Slice Tests")
class EmployeeRepositoryTest {

    @Autowired private TestEntityManager  entityManager;
    @Autowired private EmployeeRepository employeeRepository;

    private Department department;
    private Role       role;
    private Employee   employee;

    @BeforeEach
    void setUp() {
        department = entityManager.persistAndFlush(
                Department.builder()
                        .departmentName("Engineering")
                        .departmentCode("ENG")
                        .build());

        role = entityManager.persistAndFlush(
                Role.builder()
                        .roleName("Engineer")
                        .roleDescription("Software engineer")
                        .build());

        employee = entityManager.persistAndFlush(
                Employee.builder()
                        .firstName("Alice").lastName("Johnson")
                        .email("alice.j@test.com")
                        .phone("9876543210")
                        .salary(new BigDecimal("90000.00"))
                        .joiningDate(LocalDate.of(2022, 6, 1))
                        .status(EmployeeStatus.ACTIVE)
                        .department(department).role(role)
                        .isDeleted(false)
                        .build());

        entityManager.clear(); // Ensure fetches hit the database
    }

    @Test
    @DisplayName("existsByEmail returns true for existing email")
    void existsByEmail_True() {
        assertThat(employeeRepository.existsByEmail("alice.j@test.com")).isTrue();
    }

    @Test
    @DisplayName("existsByEmail returns false for unknown email")
    void existsByEmail_False() {
        assertThat(employeeRepository.existsByEmail("unknown@test.com")).isFalse();
    }

    @Test
    @DisplayName("findByEmail returns employee when present")
    void findByEmail_Found() {
        Optional<Employee> found = employeeRepository.findByEmail("alice.j@test.com");
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Alice");
    }

    @Test
    @DisplayName("searchByName finds employee by partial first name")
    void searchByName_ByFirstName() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> result = employeeRepository.searchByName("Alice", pageable);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getEmail()).isEqualTo("alice.j@test.com");
    }

    @Test
    @DisplayName("searchByName finds employee by partial last name")
    void searchByName_ByLastName() {
        Page<Employee> result = employeeRepository.searchByName("Johnson", PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("searchByName returns empty for non-matching name")
    void searchByName_NoMatch() {
        Page<Employee> result = employeeRepository.searchByName("Xyz", PageRequest.of(0, 10));
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("findByDepartmentId returns employees for that department")
    void findByDepartmentId_Found() {
        Page<Employee> result = employeeRepository.findByDepartmentId(
                department.getId(), PageRequest.of(0, 10));
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDepartment().getDepartmentCode()).isEqualTo("ENG");
    }

    @Test
    @DisplayName("findByStatus returns only ACTIVE employees")
    void findByStatus_Active() {
        Page<Employee> result = employeeRepository.findByStatus(
                EmployeeStatus.ACTIVE, PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
        assertThat(result.getContent()).allMatch(e -> e.getStatus() == EmployeeStatus.ACTIVE);
    }

    @Test
    @DisplayName("Soft-deleted employee is excluded from default queries")
    void softDelete_ExcludedFromQueries() {
        // Soft-delete the employee directly
        employee.softDelete();
        entityManager.persistAndFlush(employee);
        entityManager.clear();

        // @SQLRestriction on entity should exclude it
        Optional<Employee> found = employeeRepository.findByEmail("alice.j@test.com");
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("existsByEmailAndIdNot excludes current entity from uniqueness check")
    void existsByEmailAndIdNot_ExcludesSelf() {
        boolean exists = employeeRepository.existsByEmailAndIdNot(
                "alice.j@test.com", employee.getId());
        assertThat(exists).isFalse(); // Same email, same id → not a duplicate
    }

    @Test
    @DisplayName("findByIdWithDetails eager-fetches department and role")
    void findByIdWithDetails_FetchesDepartmentAndRole() {
        Optional<Employee> result = employeeRepository.findByIdWithDetails(employee.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getDepartment().getDepartmentName()).isEqualTo("Engineering");
        assertThat(result.get().getRole().getRoleName()).isEqualTo("Engineer");
    }
}
