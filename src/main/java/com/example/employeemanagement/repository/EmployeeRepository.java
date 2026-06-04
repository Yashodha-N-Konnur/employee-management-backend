package com.example.employeemanagement.repository;

import com.example.employeemanagement.entity.Employee;
import com.example.employeemanagement.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access layer for {@link Employee}.
 *
 * <p>Note: The {@code @SQLRestriction("is_deleted = false")} on the entity means
 * ALL queries here automatically exclude soft-deleted employees.
 */
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ── Uniqueness checks ──────────────────────────────────────────────────
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);

    // ── Lookup ─────────────────────────────────────────────────────────────
    Optional<Employee> findByEmail(String email);

    // ── Search ─────────────────────────────────────────────────────────────
    /** Full-name search; uses CONCAT to match firstName + lastName. */
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(CONCAT(e.firstName, ' ', e.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Employee> searchByName(@Param("name") String name, Pageable pageable);

    /** Search employees by department ID – paginated. */
    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    /** Filter employees by role ID – paginated. */
    Page<Employee> findByRoleId(Long roleId, Pageable pageable);

    /** Filter by status. */
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    /** Combined search – department + role. */
    Page<Employee> findByDepartmentIdAndRoleId(Long departmentId, Long roleId, Pageable pageable);

    /** Count active employees per department (for HR reports). */
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.department.id = :deptId AND e.status = 'ACTIVE'")
    long countActiveByDepartmentId(@Param("deptId") Long deptId);

    // ── JPQL join-fetch to avoid N+1 on list endpoints ────────────────────
    @Query("SELECT e FROM Employee e " +
           "LEFT JOIN FETCH e.department " +
           "LEFT JOIN FETCH e.role " +
           "WHERE e.id = :id")
    Optional<Employee> findByIdWithDetails(@Param("id") Long id);

    // ── Native query example – bulk status update ──────────────────────────
    @Modifying
    @Query("UPDATE Employee e SET e.status = :status WHERE e.department.id = :deptId")
    int updateStatusByDepartment(@Param("deptId") Long deptId,
                                 @Param("status") EmployeeStatus status);

    List<Employee> findByDepartmentId(Long departmentId);

    List<Employee> findByRoleId(Long roleId);
}
