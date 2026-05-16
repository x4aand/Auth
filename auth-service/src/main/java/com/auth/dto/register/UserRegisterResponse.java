package com.auth.dto.register;

import com.auth.entity.UserEntity;

import java.util.UUID;

/**
 * DTO for {@link UserEntity}
 */
public record UserRegisterResponse(UUID uuid,
                                   String username,
                                   String email) {


}