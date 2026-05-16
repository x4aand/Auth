package com.auth.dto.role;

import com.auth.entity.UserEntity;

import java.util.Set;

/**
 * DTO for {@link UserEntity}
 */
public record UserRoleSetResponse(
        Set<UserRoleResponse> rolesResponse
) {}

