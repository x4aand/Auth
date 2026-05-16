package com.auth.controller;

import com.auth.dto.login.UserLoginRequest;
import com.auth.dto.login.UserLoginResponse;
import com.auth.dto.refresh.RefreshResponse;
import com.auth.dto.register.UserRegisterRequest;
import com.auth.service.LoginService;
import com.auth.service.RegistrationService;
import com.auth.service.jwt.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")

public class AuthController {

    private final RegistrationService registrationService;
    private final LoginService loginService;
    private final JWTService jwtService;

    @PostMapping("/registration")
    public ResponseEntity<?> registration( // будем возвращаеть ответ с помощью ResponseEntity
                                           @RequestBody UserRegisterRequest UserRegistrationEntityDto // получаем данные для регистрации
    ) {
        try {

            log.info("Поступил запрос в registration");

            registrationService.registration(UserRegistrationEntityDto); // регистрация

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "success", true,
                            "message", "Регистрация прошла успешно "
                    ));

        } catch (Exception e) {
                log.error("Registration: Ошибка " + e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Регистрация прошла  не успешно "
                    ));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest
                                   ) {
        try {
            UserLoginResponse loginResponse = loginService.login(userLoginRequest);

            return ResponseEntity
                    .ok()
                    .header(HttpHeaders.SET_COOKIE, loginResponse.refreshTokenCookie())
                    .header("X-User-ID" + loginResponse.uuid())
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + loginResponse.accesToken())
                    .body(loginResponse.data());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("success", false, "message", "Неверные данные")
            );
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("refresh_token") String refreshToken){
      try {
         RefreshResponse refreshResponse = jwtService.refreshTokens(refreshToken);
          log.info("REFRESH COOKIE: {}", refreshToken);
         return ResponseEntity
                 .status(HttpStatus.OK)
                 .header(HttpHeaders.SET_COOKIE,refreshResponse.refreshToken())
                 .header("X-User-ID", refreshResponse.uuid())
                 .header(HttpHeaders.AUTHORIZATION, "Bearer " + refreshResponse.accesToken())
                 .body(
                         Map.of("success", true, "message", "Успешный рефрешь")
                 );

      } catch (Exception e) {
          log.error("REFRESH ERROR", e);
          return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                  Map.of("success", false, "message", "Неверные данные")
          );
      }
    }

}
