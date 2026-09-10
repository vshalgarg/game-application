package com.codemonks.api_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class UserIdPropagationFilter implements GlobalFilter, Ordered {

    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        return exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> {

                    if (authentication instanceof JwtAuthenticationToken jwtAuth) {

                        String userId =
                                jwtAuth.getToken()
                                        .getClaimAsString("userId");

                        if (userId != null && !userId.isBlank()) {

                            ServerHttpRequest request =
                                    exchange.getRequest()
                                            .mutate()
                                            .headers(headers -> {
                                                headers.remove(USER_ID_HEADER);
                                                headers.add(
                                                        USER_ID_HEADER,
                                                        userId
                                                );
                                            })
                                            .build();

                            return chain.filter(
                                    exchange.mutate()
                                            .request(request)
                                            .build()
                            );
                        }
                    }

                    return chain.filter(exchange);
                })
                .switchIfEmpty(chain.filter(exchange));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}