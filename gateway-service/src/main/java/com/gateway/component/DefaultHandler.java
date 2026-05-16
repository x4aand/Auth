package com.gateway.component;

import com.gateway.dto.ParserJwtTokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class DefaultHandler {

    private final GatewayComponent gatewayService;

    public Mono<Void> handler(ServerWebExchange exchange, GatewayFilterChain chain) {
                return Mono.defer(() -> {
                    String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    ServerHttpRequest request = exchange.getRequest();
                    HttpCookie refreshCookie = request.getCookies().getFirst("refresh_token");

                    log.info("=== INCOMING REQUEST ===");
                    log.info("PATH: " + request.getPath());
                    log.info("AUTH HEADER: " + authHeader);

                    if (refreshCookie != null) {
                        log.info("REFRESH COOKIE: " + refreshCookie.getValue());
                    } else {
                        log.warn("REFRESH COOKIE: NULL ❌");
                    }

                    log.info("ALL HEADERS: " + request.getHeaders());
                    log.info("ALL COOKIES: " + request.getCookies());

                    if (authHeader == null || !authHeader.startsWith("Bearer") || refreshCookie == null) {
                        log.error("❌ AUTH FAILED");
                        log.error("authHeader = " + authHeader);
                        log.error("refreshCookie = " + refreshCookie);
                        return onError(exchange, "Отсутствует заголовок авторизации или рефрешь токен", HttpStatus.UNAUTHORIZED);
                    }


                    String accesstoken = authHeader.substring(7);
                    log.info("Token: " + accesstoken);

                    ParserJwtTokenDTO parserJwtTokenDTO = gatewayService.validationAccessToken(accesstoken);

                    log.info("✅ TOKEN VALID, USER: " + parserJwtTokenDTO.username());
                    // Сохраняем username в атрибуты exchange — для WS handshake
                    exchange.getAttributes().put("X-Internal-Username", parserJwtTokenDTO.username());


                    exchange.getResponse()
                            .getHeaders()
                            .remove("X-Internal-Username");

                    exchange.getResponse()
                            .getHeaders()
                            .add("X-Internal-Username", parserJwtTokenDTO.username());

                    exchange.getResponse()
                            .getHeaders()
                            .remove("X-Internal-UUID");

                    exchange.getResponse()
                            .getHeaders()
                            .add("X-Internal-UUID", parserJwtTokenDTO.username());

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-Internal-Username", parserJwtTokenDTO.username())
                            .header("X-Internal-UUID", parserJwtTokenDTO.uuid())
                            .build();

                    return  chain.filter(exchange.mutate().request(mutatedRequest).build());

        }).onErrorResume(ex -> {
            // ловим все исключения из validateAccessToken
            if (ex instanceof GatewayComponent.TokenExpiredException) {
                return onError(exchange, "TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);
            } else if (ex instanceof GatewayComponent.InvalidTokenException) {
                return onError(exchange, "TOKEN_INVALID", HttpStatus.FORBIDDEN);
            } else {
                return onError(exchange, "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        });
    }

    private Mono<Void> onError(ServerWebExchange exchange,
                               String error,
                               HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("X-Auth-Error", error);
        return exchange.getResponse().setComplete();
    }
}
