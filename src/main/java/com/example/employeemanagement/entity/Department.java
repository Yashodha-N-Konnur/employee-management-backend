package com.example.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a department within the organisation.
 *
 * Relationship: One Department → Many Employees
 * CascadeType.ALL allows saving/deleting employees with their department.
 * FetchType.LAZY defers loading employees until explicitly accessed (performance best practice).
 */
@Entity
@Table(name = "departments",
       uniqueConstraints = @UniqueConstraint(columnNames = "department_code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    @Column(name = "department_code", nullable = false, unique = true, length = 20)
    private String departmentCode;

    /**
     * Bidirectional OneToMany – mappedBy references the "department" field in Employee.
     * orphanRemoval = true ensures employees are deleted if removed from this list.
     */
    @OneToMany(
        mappedBy    = "department",
        cascade     = CascadeType.ALL,
        fetch       = FetchType.LAZY,
        orphanRemoval = false
    )
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();

    // ── Convenience helpers ────────────────────────────────────────────────
    public void addEmployee(Employee employee) {
        employees.add(employee);
        employee.setDepartment(this);
    }

    public void removeEmployee(Employee employee) {
        employees.remove(employee);
        employee.setDepartment(null);
    }
}
