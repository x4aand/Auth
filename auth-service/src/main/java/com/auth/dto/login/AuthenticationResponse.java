package com.auth.dto.login;

import com.auth.dto.role.UserRoleSetResponse;
import com.auth.entity.UserEntity;

/**
 * DTO for {@link UserEntity}
 */
public record AuthenticationResponse(
        String username,
        String email,
        UserRoleSetResponse userLoginRoleMapper // роли
) { }