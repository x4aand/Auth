package com.auth.dto.login;

public record UserLoginRequest(String username,
                               String password,
                               String email) {
}