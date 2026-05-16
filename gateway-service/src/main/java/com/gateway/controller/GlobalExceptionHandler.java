package com.gateway.controller;

import com.gateway.component.GatewayComponent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // на рефрешь
    @ExceptionHandler(GatewayComponent.TokenExpiredException.class)
    public ResponseEntity<?> handleExpired(GatewayComponent.TokenExpiredException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // на рефрешь
                .body(Map.of(
                        "error", "TOKEN_EXPIRED",
                        "message", e.getMessage()
                ));
    }

    // на повторный логин
    @ExceptionHandler(GatewayComponent.InvalidTokenException.class)
    public ResponseEntity<?> handleInvalid(GatewayComponent.InvalidTokenException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN) //  403 поддельные токен
                .body(Map.of(
                        "error", "TOKEN_INVALID",
                        "message", e.getMessage()
                ));
    }

}

