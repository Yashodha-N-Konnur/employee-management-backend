package com.example.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Central entity representing an employee.
 *
 * Relationships:
 *   - ManyToOne → Department  (many employees belong to one department)
 *   - ManyToOne → Role        (many employees share one role)
 *
 * Soft-delete pattern: {@code isDeleted} flag + {@code deletedAt} timestamp.
 * {@code @SQLRestriction} ensures deleted employees are excluded from all queries.
 */
@Entity
@Table(name = "employees",
       uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@SQLRestriction("is_deleted = false")   // Hibernate 6 – filters soft-deleted rows globally
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "salary", precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    // ── Relationships ──────────────────────────────────────────────────────

    /**
     * EAGER is avoided here; LAZY is the JPA default and best-practice for ManyToOne.
     * Department is loaded only when accessed explicitly.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    // ── Soft Delete ────────────────────────────────────────────────────────

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /**
     * Performs a soft delete by marking the entity as deleted.
     * The actual DB row is preserved for auditing / compliance.
     */
    public void softDelete() {
        this.isDeleted  = true;
        this.deletedAt  = LocalDateTime.now();
        this.status     = EmployeeStatus.INACTIVE;
    }
}
