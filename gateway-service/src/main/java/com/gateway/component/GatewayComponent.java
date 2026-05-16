package com.gateway.component;

import com.gateway.dto.ParserJwtTokenDTO;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.PublicKey;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayComponent {

    private final PublicKey publicKey;

    public ParserJwtTokenDTO validationAccessToken(String tokenAccess) {

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(tokenAccess)
                    .getPayload();

            String uuid = claims.getSubject();
            String username = claims.get("name", String.class);

            log.info("Токен валиден для пользователя: {} c логином {}", uuid, username);
            ParserJwtTokenDTO parserJwtTokenDTO = new ParserJwtTokenDTO(uuid, username);
            return  parserJwtTokenDTO;

        } catch (ExpiredJwtException e) {

            log.warn("Токен истёк: {}", e.getMessage());
            throw new TokenExpiredException("Токен истёк");

        } catch (SignatureException e) {

            log.error("Подпись токена невалидна: {}", e.getMessage());
            throw new InvalidTokenException("Токен подделан");

        } catch (MalformedJwtException e) {

            log.error("Токен повреждён: {}", e.getMessage());
            throw new InvalidTokenException("Токен повреждён");

        } catch (IllegalArgumentException e) {

            log.error("Токен пустой: {}", e.getMessage());
            throw new InvalidTokenException("Токен отсутствует");

        } catch (JwtException e) {

            log.error("Ошибка JWT: {}", e.getMessage());
            throw new InvalidTokenException("Невалидный токен");

        } catch (Exception e) {

            log.error("Неизвестная ошибка", e);
            throw new InvalidTokenException("Ошибка токена");
        }
    }



    // TokenExpiredException
    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException(String message) {
            super(message);
        }
    }

    // InvalidTokenException
    public static class InvalidTokenException extends RuntimeException {
        public InvalidTokenException(String message) {
            super(message);
        }
    }
}
