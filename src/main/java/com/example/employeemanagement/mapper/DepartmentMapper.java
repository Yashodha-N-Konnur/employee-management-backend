package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.request.DepartmentRequestDto;
import com.example.employeemanagement.dto.response.DepartmentResponseDto;
import com.example.employeemanagement.entity.Department;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DepartmentMapper {


@Mapping(target = "employeeCount", ignore = true)
DepartmentResponseDto toResponseDto(Department department);

@Mapping(target = "id", ignore = true)
@Mapping(target = "employees", ignore = true)
Department toEntity(DepartmentRequestDto dto);

@BeanMapping(
    nullValuePropertyMappingStrategy =
        NullValuePropertyMappingStrategy.IGNORE
)
@Mapping(target = "id", ignore = true)
@Mapping(target = "employees", ignore = true)
void updateEntityFromDto(
    DepartmentRequestDto dto,
    @MappingTarget Department department
);

}
