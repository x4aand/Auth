package com.auth.dto.role;

import com.auth.entity.UserEntity;
import com.auth.entity.UserRoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    @Mapping(source = "roles", target = "rolesResponse") // преобразуем из UserEntity roles в rolesResponse
    UserRoleSetResponse toResponse(UserEntity user);

    UserRoleResponse toRoleResponse(UserRoleEntity userRoleEntity);
}
