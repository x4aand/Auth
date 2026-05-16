package com.auth.dto.refresh;

public record RefreshResponse(
        String accesToken,
        String refreshToken,
        String uuid
) {}
