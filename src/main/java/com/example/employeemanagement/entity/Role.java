package com.example.employeemanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a functional role/job title within the organisation.
 *
 * Relationship: One Role → Many Employees
 */
@Entity
@Table(name = "roles",
       uniqueConstraints = @UniqueConstraint(columnNames = "role_name"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

    @Column(name = "role_description", length = 255)
    private String roleDescription;

    @OneToMany(
        mappedBy = "role",
        cascade  = CascadeType.ALL,
        fetch    = FetchType.LAZY
    )
    @Builder.Default
    private List<Employee> employees = new ArrayList<>();
}
