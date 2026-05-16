package com.auth.service.jwt;

import com.auth.dto.refresh.RefreshResponse;
import com.auth.dto.register.UserRegisterResponse;
import com.auth.entity.UserEntity;
import com.auth.repository.UserEntityRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTService {

    private final UserEntityRepository userEntityRepository;

    @Value("${jwt.private-key}")
    private String privateKeyPem;

    @Value("${jwt.public-key}")
    private String publicKeyPem;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;

    @PostConstruct
    private void init(){
        try {
            this.privateKey = loadPrivateKey(privateKeyPem);
            this.publicKey = loadPublicKey(publicKeyPem);
            log.info("JWTService инициализация jwt токенов прошла успешно");

        } catch (Exception e) {
            log.error("Ошибка в jwt не были загружены ключи", e);
            throw new RuntimeException("Ошибка инициализации JWTService", e);
        }
    }


    private PrivateKey loadPrivateKey(String privateKeyPem) throws Exception {
        if (privateKeyPem == null || privateKeyPem.trim().isEmpty()) {
            log.error("JWTService: privateKeyPem is null or empty");
            return null;
        }

        try {
            String keyContent = privateKeyPem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decode = Base64.getDecoder().decode(keyContent);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decode));
        } catch (Exception e) {
            log.error("Ошибка загрузки приватного ключа", e);
            throw e;
        }
    }

    private PublicKey loadPublicKey(String publicKeyPem) throws Exception {
        if (publicKeyPem == null || publicKeyPem.trim().isEmpty()) {
            log.error("JWTService: publicKeyPem is null or empty");
            return null;
        }

        try {
            String keyContent = publicKeyPem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s+", "");

            byte[] decode = Base64.getDecoder().decode(keyContent);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(decode);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            log.error("Ошибка загрузки публичного ключа", e);
            throw e;
        }
    }



    @Transactional
    public String generateJWTTAccessTokens(UUID uuid, String name) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant accessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant(); // 5 минут дествует токен Access
        final Date accessExpiration = Date.from(accessExpirationInstant);

        if (uuid == null) {
            log.error("UUID пользователя не может быть null");
            throw new IllegalArgumentException("UUID пользователя не может быть null");
        }
        if (name == null || name.isEmpty()) {
            log.error("Имя пользователя не может быть пустым");
            throw new IllegalArgumentException("Имя пользователя не может быть пустым");
        }

        try {
            return Jwts.builder()
                    .header()
                    .keyId("key-2026")
                    .and()
                    .subject(uuid.toString())
                    .expiration(accessExpiration)
                    .claim("name", name)
                    .signWith(privateKey, SignatureAlgorithm.RS256)
                    .compact();

        } catch (Exception e) {
            log.error("Ошибка генерации JWT токена для пользователя: {}", uuid, e);
            throw new RuntimeException("Ошибка генерации токена", e);
        }
    }

    @Transactional
    public String generateJWTTRefreshTokens(String name){

        try {
            UserEntity user = userEntityRepository.findByUsername(name)
                    .orElseThrow(() -> new UsernameNotFoundException("Не найден: " + name));

            // Генерируем secure refresh token
            byte[] randomBytes = new byte[32];
            SecureRandom.getInstanceStrong().nextBytes(randomBytes);
            String refreshToken = Base64.getUrlEncoder().encodeToString(randomBytes);

            final LocalDateTime now = LocalDateTime.now();

            // 7 дней срока действия
            final Instant expirationInstant = now.plusDays(7).atZone(ZoneId.systemDefault()).toInstant();
            final Date expiration = Date.from(expirationInstant);
            final Date created = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());

            // реализовать БД PostgreSQL

            // Ключ: refreshToken:UUID, Значение: сам токен, TTL: 7 дней
            redisTemplate.opsForValue().set(
                    "refreshToken:" + refreshToken,
                    user.getUuid().toString(),
                    7, TimeUnit.DAYS
                    );

            log.info("Refresh токен сгенерирован и сохранен для пользователя: {}", name);

            return refreshToken;

        } catch (Exception e) {
            log.error("ОШИБКА В JWT_SERVICE: ", e);
            throw new RuntimeException(e);
        }
    }


    public ResponseCookie generateRefreshCookie(String refreshToken){

        return ResponseCookie.from("refresh_token", refreshToken) // имя
                .httpOnly(true) // js не сможет прочитать данный куки
                .secure(false) // HTTPS передаются данные  ВКЛЮЧИТЬ ПРИ ПРОДАКШЕНЕ
                .path("/") // применятся ко всем поинтам
                .maxAge(7 * 24 * 60 * 60) // 7 дней действует
                .sameSite("Lax") // защита CSRF атак
                .build();
    }

    @Transactional
    public boolean validationJwtToken(String accesToken){
        try {

            // удаляем кавычки
            String cleanedToken = accesToken
                    .replace("\"", "")
                    .trim();

            // парсем токен  по публичному ключу
            Jwts.parser()
                    .verifyWith(publicKey) // RSAPublicKey
                    .build()
                    .parseSignedClaims(cleanedToken)
                    .getPayload().getSubject();

            return true;

        } catch (JwtException e){
            log.error("Token не валидный" + e);
            return false;
        }
    }

    @Transactional
    public RefreshResponse refreshTokens (String refreshToken){

        if(refreshToken == null){
            throw new IllegalArgumentException("Не поступил рефрешь тиокен ");
        }
        String decodedToken = URLDecoder.decode(refreshToken, StandardCharsets.UTF_8);
        String key = "refreshToken:" + decodedToken;
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            throw new IllegalArgumentException("В бд токена нет рефрешь");
        }

        String uuid = value.toString();
        UUID userUUID = UUID.fromString(uuid);


        UserEntity user = userEntityRepository.findById(userUUID)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + userUUID));

        // выдаем новые токены
        String accessJWTTToken =  generateJWTTAccessTokens(user.getUuid(), user.getUsername());
        String refreshJWTTToken = generateJWTTRefreshTokens(user.getUsername());
        ResponseCookie refreshTokenCookie = generateRefreshCookie(refreshJWTTToken);
        RefreshResponse refreshResponse = new RefreshResponse(accessJWTTToken, refreshTokenCookie.toString(), user.getUuid().toString());

        redisTemplate.delete(key); // только потом удаляем токена

        return  refreshResponse;
    }
}

