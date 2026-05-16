package com.gateway.component;

import com.gateway.dto.ParserJwtTokenDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;

import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthHandler {

    private final GatewayComponent gatewayComponent;

    public Mono<Void> loginHandle (ServerWebExchange exchange, GatewayFilterChain chain){
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();

                })
        );
    }

    public Mono<Void> registrationHandle(ServerWebExchange exchange, GatewayFilterChain chain){
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();

                })
        );
    }

    public Mono<Void> refreshHandle (ServerWebExchange exchange, GatewayFilterChain chain){

        ServerHttpRequest request = exchange.getRequest();
        HttpCookie refreshCookie = request.getCookies().getFirst("refresh_token");

        if (refreshCookie == null) {
            throw new GatewayComponent.InvalidTokenException("Токен не валидный");
        }


        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();

                })
        );
    }

    public Mono<Void> wsHandle(ServerWebExchange exchange, GatewayFilterChain chain) {

        return Mono.defer(() -> {

            ServerHttpRequest request = exchange.getRequest();
            log.info("WS handle called, path: {}", request.getPath());
            log.info("Upgrade header: {}", request.getHeaders().getFirst("Upgrade"));
            log.info("Token param: {}", request.getQueryParams().getFirst("token"));
            log.info("All query params: {}", request.getQueryParams());

            boolean isWebSocket = "websocket".equalsIgnoreCase(
                    request.getHeaders().getFirst("Upgrade")
            );

            if (!isWebSocket) {
                return chain.filter(exchange);
            }

            // берём токен из query
            String accessToken = request.getQueryParams().getFirst("token");

            if (accessToken == null) {
                return onError(exchange, "NO_TOKEN", HttpStatus.UNAUTHORIZED);
            }

            log.info("WS Token: {}", accessToken);

            // валидируем JWT
            ParserJwtTokenDTO parsed = gatewayComponent.validationAccessToken(accessToken);

            // пробрасываем username внутрь Netty
            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-Internal-Username", parsed.username())
                    .header("X-User-Id", parsed.uuid())
                    .build();



            return  chain.filter(exchange.mutate().request(mutatedRequest).build());

        }).onErrorResume(ex -> {

            if (ex instanceof GatewayComponent.TokenExpiredException) {
                return onError(exchange, "TOKEN_EXPIRED", HttpStatus.UNAUTHORIZED);
            }

            if (ex instanceof GatewayComponent.InvalidTokenException) {
                return onError(exchange, "TOKEN_INVALID", HttpStatus.FORBIDDEN);
            }

            return onError(exchange, "INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
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