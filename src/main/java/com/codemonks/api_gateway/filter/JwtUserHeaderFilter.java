//package com.codemonks.api_gateway.filter;
//
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//@Component
//public class JwtUserHeaderFilter implements GlobalFilter, Ordered {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//
//        return ReactiveSecurityContextHolder.getContext()
//                .map(SecurityContext::getAuthentication)
//
//                .flatMap(authentication -> {
//                    Jwt jwt = (Jwt) authentication.getPrincipal();
//                    String userId = jwt.getClaimAsString("userId");
//                    String username = jwt.getClaimAsString("sub");
//                    ServerHttpRequest mutatedRequest =
//                            exchange.getRequest()
//                                    .mutate()
//                                    .header("X-User-Id", userId)
//                                    .header("X-Username", username)
//                                    .build();
//
//                    return chain.filter(
//                            exchange.mutate()
//                                    .request(mutatedRequest)
//                                    .build()
//                    );
//                })
//                .switchIfEmpty(chain.filter(exchange));
//    }
//
//    @Override
//    public int getOrder() {
//        return 0;
//    }
//}
