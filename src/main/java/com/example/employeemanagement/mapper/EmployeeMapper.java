
package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.request.EmployeeRequestDto;
import com.example.employeemanagement.dto.response.EmployeeResponseDto;
import com.example.employeemanagement.entity.Employee;
import org.mapstruct.*;

/**
 * MapStruct mapper for Employee ↔ DTO conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmployeeMapper {

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.departmentName", target = "departmentName")
    @Mapping(source = "department.departmentCode", target = "departmentCode")
    @Mapping(source = "role.id", target = "roleId")
    @Mapping(source = "role.roleName", target = "roleName")
    @Mapping(
        target = "fullName",
        expression = "java(employee.getFirstName() + \" \" + employee.getLastName())"
    )
    EmployeeResponseDto toResponseDto(Employee employee);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    Employee toEntity(EmployeeRequestDto dto);

    @BeanMapping(
        nullValuePropertyMappingStrategy =
            NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isDeleted", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    void updateEntityFromDto(
        EmployeeRequestDto dto,
        @MappingTarget Employee employee
    );
}

