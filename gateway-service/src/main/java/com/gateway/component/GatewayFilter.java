package com.gateway.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class GatewayFilter implements GlobalFilter , Ordered {

    String TAG = "Gateway";
    private final AuthHandler authHandler;
    private final  DefaultHandler defaultHandler;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getPath().value();
        log.info(TAG + " путь " + path);

        return switch (path) {
            case "/api/auth/login" -> authHandler.loginHandle(exchange, chain);
            case "/api/auth/registration" -> authHandler.registrationHandle(exchange, chain);
            case "/api/auth/refresh" -> authHandler.refreshHandle(exchange, chain);
            case "/ws" -> authHandler.wsHandle(exchange, chain);

            default -> defaultHandler.handler(exchange, chain)
                    .onErrorResume(GatewayComponent.TokenExpiredException.class, e -> {
                        log.warn("Токен истёк, возвращаем 401");
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        exchange.getResponse().getHeaders().add("X-Auth-Error", "TOKEN_EXPIRED");
                        return exchange.getResponse().setComplete();
                    })
                    .onErrorResume(GatewayComponent.InvalidTokenException.class, e -> {
                        log.error("Токен невалиден, возвращаем 403");
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        exchange.getResponse().getHeaders().add("X-Auth-Error", "TOKEN_INVALID");
                        return exchange.getResponse().setComplete();
                    });
        };
    }


    @Override
    public int getOrder() {
        return 0;
    }
}
