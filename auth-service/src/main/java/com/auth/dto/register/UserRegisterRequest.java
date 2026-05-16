package com.auth.dto.register;

import com.auth.entity.UserEntity;

/**
 * DTO for {@link UserEntity} реквест для регистрации
 */
public record UserRegisterRequest(String username,
                                  String password,
                                  String email) {
}