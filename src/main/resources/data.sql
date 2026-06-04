-- Sample Data for Employee Management System
-- Run AFTER schema creation (spring.jpa.hibernate.ddl-auto=update or create)

-- Departments
INSERT IGNORE INTO departments (department_name, department_code, created_at, updated_at)
VALUES
    ('Engineering', 'ENG', NOW(), NOW()),
    ('Human Resources', 'HR', NOW(), NOW()),
    ('Finance', 'FIN', NOW(), NOW()),
    ('Marketing', 'MKT', NOW(), NOW()),
    ('Operations', 'OPS', NOW(), NOW());

-- Roles
INSERT IGNORE INTO roles (role_name, role_description, created_at, updated_at)
VALUES
    ('Software Engineer', 'Develops and maintains software applications', NOW(), NOW()),
    ('Senior Software Engineer', 'Leads technical development of complex systems', NOW(), NOW()),
    ('HR Manager', 'Manages human resources and talent acquisition', NOW(), NOW()),
    ('Financial Analyst', 'Analyzes financial data and creates reports', NOW(), NOW()),
    ('Marketing Specialist', 'Executes marketing campaigns and strategies', NOW(), NOW()),
    ('Team Lead', 'Leads and mentors a team of engineers', NOW(), NOW());

-- Employees
INSERT IGNORE INTO employees (first_name, last_name, email, phone, salary, joining_date, status, department_id, role_id, is_deleted, created_at, updated_at)
VALUES
    ('John',    'Smith',    'john.smith@company.com',    '9876543210', 85000.00,  '2022-01-15', 'ACTIVE', 1, 1, false, NOW(), NOW()),
    ('Jane',    'Doe',      'jane.doe@company.com',      '9876543211', 95000.00,  '2021-06-01', 'ACTIVE', 1, 2, false, NOW(), NOW()),
    ('Robert',  'Johnson',  'robert.j@company.com',      '9876543212', 75000.00,  '2023-03-10', 'ACTIVE', 2, 3, false, NOW(), NOW()),
    ('Emily',   'Davis',    'emily.davis@company.com',   '9876543213', 70000.00,  '2022-09-20', 'ACTIVE', 3, 4, false, NOW(), NOW()),
    ('Michael', 'Wilson',   'michael.w@company.com',     '9876543214', 68000.00,  '2023-01-05', 'ACTIVE', 4, 5, false, NOW(), NOW()),
    ('Sarah',   'Brown',    'sarah.brown@company.com',   '9876543215', 110000.00, '2020-04-15', 'ACTIVE', 1, 6, false, NOW(), NOW()),
    ('David',   'Lee',      'david.lee@company.com',     '9876543216', 88000.00,  '2021-11-30', 'ON_LEAVE', 1, 1, false, NOW(), NOW()),
    ('Lisa',    'Taylor',   'lisa.taylor@company.com',   '9876543217', 72000.00,  '2022-07-22', 'ACTIVE', 5, 5, false, NOW(), NOW());
