package com.auth.service;

import com.auth.dto.login.AuthenticationResponse;
import com.auth.dto.login.UserLoginRequest;
import com.auth.dto.login.UserLoginResponse;
import com.auth.dto.role.UserRoleMapper;
import com.auth.entity.UserEntity;
import com.auth.repository.UserEntityRepository;
import com.auth.service.jwt.JWTService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginService  {

    private final AuthenticationManager authenticationManager;

    private final UserEntityRepository userEntityRepository;

    private final UserRoleMapper userRoleMapper;

    private final JWTService jwtService;

    @Transactional
    public UserLoginResponse<AuthenticationResponse> login(UserLoginRequest userLoginRequest) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userLoginRequest.username(), userLoginRequest.password())
            );

            UserEntity user = userEntityRepository.findByUsername(userLoginRequest.username())
                    .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + userLoginRequest.username()));

            log.info("Аутентификация успешна для пользователя: {}", userLoginRequest.username());

            AuthenticationResponse authResponse = new AuthenticationResponse(
                    user.getUsername(),
                    user.getEmail(),
                    userRoleMapper.toResponse(user) // получаем роли из DTO
            );

            String accessJWTTToken =  jwtService.generateJWTTAccessTokens(user.getUuid(), user.getUsername());
            String refreshJWTTToken = jwtService.generateJWTTRefreshTokens(user.getUsername());
            ResponseCookie refreshTokenCookie = jwtService.generateRefreshCookie(refreshJWTTToken);

            return new UserLoginResponse<>(
                    true,
                    "Успешный вход UserLoginResponse",
                    authResponse,
                    accessJWTTToken,
                    refreshJWTTToken,
                    refreshTokenCookie.toString(),
                    user.getUuid().toString()
                    );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
