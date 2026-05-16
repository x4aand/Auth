package com.auth.dto.role;

public record UserRoleResponse(
        String roleName,
        String displayName,
        String description
) {
}
