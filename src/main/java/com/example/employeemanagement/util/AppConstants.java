package com.example.employeemanagement.util;

/**
 * Application-wide constants.
 * Add new constants here; never scatter magic strings across layers.
 */
public final class AppConstants {

    private AppConstants() { /* utility class – no instantiation */ }

    // ── API ────────────────────────────────────────────────────────────────
    public static final String API_BASE    = "/api/v1";
    public static final String EMPLOYEES   = API_BASE + "/employees";
    public static final String DEPARTMENTS = API_BASE + "/departments";
    public static final String ROLES       = API_BASE + "/roles";
    public static final String AUTH        = API_BASE + "/auth";

    // ── Pagination defaults ────────────────────────────────────────────────
    public static final int    DEFAULT_PAGE_NUMBER = 0;
    public static final int    DEFAULT_PAGE_SIZE   = 10;
    public static final String DEFAULT_SORT_BY     = "id";
    public static final String DEFAULT_SORT_DIR    = "asc";

    // ── Messages ───────────────────────────────────────────────────────────
    public static final String EMPLOYEE_CREATED   = "Employee created successfully";
    public static final String EMPLOYEE_UPDATED   = "Employee updated successfully";
    public static final String EMPLOYEE_DELETED   = "Employee deleted successfully";
    public static final String EMPLOYEE_FETCHED   = "Employee fetched successfully";
    public static final String EMPLOYEES_FETCHED  = "Employees fetched successfully";

    public static final String DEPT_CREATED  = "Department created successfully";
    public static final String DEPT_UPDATED  = "Department updated successfully";
    public static final String DEPT_DELETED  = "Department deleted successfully";
    public static final String DEPT_FETCHED  = "Department fetched successfully";
    public static final String DEPTS_FETCHED = "Departments fetched successfully";

    public static final String ROLE_CREATED  = "Role created successfully";
    public static final String ROLE_UPDATED  = "Role updated successfully";
    public static final String ROLE_DELETED  = "Role deleted successfully";
    public static final String ROLE_FETCHED  = "Role fetched successfully";
    public static final String ROLES_FETCHED = "Roles fetched successfully";

    // ── Error messages ─────────────────────────────────────────────────────
    public static final String EMPLOYEE_NOT_FOUND   = "Employee not found with id: ";
    public static final String DEPT_NOT_FOUND       = "Department not found with id: ";
    public static final String ROLE_NOT_FOUND       = "Role not found with id: ";
    public static final String EMAIL_ALREADY_EXISTS = "Employee with email already exists: ";
    public static final String DEPT_CODE_EXISTS     = "Department with code already exists: ";
    public static final String ROLE_NAME_EXISTS     = "Role with name already exists: ";
}
