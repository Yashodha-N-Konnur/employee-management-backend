
package com.example.employeemanagement.mapper;

import com.example.employeemanagement.dto.request.RoleRequestDto;
import com.example.employeemanagement.dto.response.RoleResponseDto;
import com.example.employeemanagement.entity.Role;
import org.mapstruct.*;

/**
 * MapStruct mapper for Role ↔ DTO conversion.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "employeeCount", ignore = true)
    RoleResponseDto toResponseDto(Role role);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    Role toEntity(RoleRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    void updateEntityFromDto(RoleRequestDto dto,
                             @MappingTarget Role role);
}

