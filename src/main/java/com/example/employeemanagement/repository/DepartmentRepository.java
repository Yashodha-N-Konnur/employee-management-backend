package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access layer for {@link Department}.
 * Spring Data JPA auto-generates implementation at runtime.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    /** Check code uniqueness before saving. */
    boolean existsByDepartmentCode(String departmentCode);

    /** Check code uniqueness excluding the current record (for updates). */
    boolean existsByDepartmentCodeAndIdNot(String departmentCode, Long id);

    Optional<Department> findByDepartmentCode(String departmentCode);

    List<Department> findByDepartmentNameContainingIgnoreCase(String name);

    /** Fetch department with eager employee list (avoids N+1 for list views). */
    @Query("SELECT d FROM Department d LEFT JOIN FETCH d.employees WHERE d.id = :id")
    Optional<Department> findByIdWithEmployees(Long id);
}
