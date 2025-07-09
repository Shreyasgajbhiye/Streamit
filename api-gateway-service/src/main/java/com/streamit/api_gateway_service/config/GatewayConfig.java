// package com.streamit.api_gateway_service.config;

// import com.streamit.apigateway.util.JwtUtil;
// import lombok.RequiredArgsConstructor;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.cloud.gateway.filter.GlobalFilter;
// import org.springframework.core.Ordered;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import reactor.core.publisher.Mono;

// @Component
// @RequiredArgsConstructor
// public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

//     private final JwtUtil jwtUtil;

//     private static final String AUTH_HEADER = "Authorization";
//     private static final String TOKEN_PREFIX = "Bearer ";

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//         ServerHttpRequest request = exchange.getRequest();

//         if (!request.getHeaders().containsKey(AUTH_HEADER)) {
//             return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
//         }

//         String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
//         if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
//             return onError(exchange, "Invalid Authorization Header", HttpStatus.UNAUTHORIZED);
//         }

//         String token = authHeader.substring(TOKEN_PREFIX.length());

//         if (!jwtUtil.isTokenValid(token)) {
//             return onError(exchange, "Invalid or Expired Token", HttpStatus.UNAUTHORIZED);
//         }

//         // Proceed with request
//         return chain.filter(exchange);
//     }

//     private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
//         exchange.getResponse().setStatusCode(status);
//         return exchange.getResponse().setComplete();
//     }

//     @Override
//     public int getOrder() {
//         return -1;
//     }
// }