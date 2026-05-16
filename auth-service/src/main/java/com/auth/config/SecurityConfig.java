package com.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("Настраиваем SecurityFilterChain...");

        http
                .authorizeHttpRequests(authz -> {
                    log.info("Разрешаем все запросы...");

                    authz
                            .requestMatchers("/api/auth/**").permitAll() // открытый
                            .requestMatchers("/error").permitAll() // ошибки
                            .anyRequest().permitAll(); // Разрешаем все остальные
                })

                .csrf(csrf -> {
                    log.info("Отключаем CSRF...");
                    csrf.disable();
                })

                .cors(cors -> {
                    log.info("Отключаем cors...");
                    cors.disable();
                })
                .formLogin(form -> {
                    log.info("Отключаем форму логина...");
                    form.disable();
                })
                .httpBasic(basic -> {
                    log.info("Отключаем HTTP Basic...");
                    basic.disable();
                });

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        log.info("Создаем CORS конфигурацию...");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

