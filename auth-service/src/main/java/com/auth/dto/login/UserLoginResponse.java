package com.auth.dto.login;

import org.springframework.http.ResponseCookie;

public record UserLoginResponse<T>(boolean success,
                                   String message,
                                   T data,
                                   String accesToken,
                                   String refreshToken,
                                   String refreshTokenCookie,
                                   String uuid) {}
