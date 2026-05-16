package com.auth.component;

import com.auth.dto.register.UserRegisterRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationMainUserService {

    private final RestTemplate restTemplate;

    public void createUser(String uuid, String username, String email) {
        final String url = "http://main-service:8084/api/main/users/create";

        log.info("Поступил запрос RegistrationMainUserService");
        try {
            Map<String, String> body = Map.of(
                    "uuid", uuid,
                    "username", username,
                    "email", email
            );

            ResponseEntity<Void> response = restTemplate.postForEntity(url, body, Void.class);

            if (response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("Main service вернул: " + response.getStatusCode());
            }

            log.info("Main service успешно создал юзера: {}", uuid);

        } catch (Exception e) {
            log.error("RegistrationMainUserService ошибка: {}", e.getMessage());
            throw new RuntimeException(e); // @Transactional откатит auth
        }
    }
}
