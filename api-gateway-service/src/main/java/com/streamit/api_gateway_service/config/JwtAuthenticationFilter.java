// package com.streamit.api_gateway_service.config;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.cloud.gateway.filter.GatewayFilterChain;
// import org.springframework.cloud.gateway.filter.GlobalFilter;
// import org.springframework.core.Ordered;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.server.reactive.ServerHttpRequest;
// import org.springframework.http.server.reactive.ServerHttpResponse;
// import org.springframework.stereotype.Component;
// import org.springframework.web.server.ServerWebExchange;
// import org.springframework.core.io.buffer.DataBuffer;

// import com.streamit.api_gateway_service.util.JwtUtil;

// import reactor.core.publisher.Mono;

// @Component
// public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

//     private final JwtUtil jwtUtil;
    
//     // Use constructor injection instead of @RequiredArgsConstructor
//     @Autowired
//     public JwtAuthenticationFilter(JwtUtil jwtUtil) {
//         this.jwtUtil = jwtUtil;
//     }

//     private static final String AUTH_HEADER = "Authorization";
//     private static final String TOKEN_PREFIX = "Bearer ";

//     @Override
//     public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//         ServerHttpRequest request = exchange.getRequest();
        
//         // Skip authentication for specific paths (health check, etc.)
//         if (isPublicPath(request.getPath().toString())) {
//             return chain.filter(exchange);
//         }

//         if (!request.getHeaders().containsKey(AUTH_HEADER)) {
//             return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
//         }

//         String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
//         if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
//             return onError(exchange, "Invalid Authorization Header Format", HttpStatus.UNAUTHORIZED);
//         }

//         String token = authHeader.substring(TOKEN_PREFIX.length()).trim();
        
//         if (token.isEmpty()) {
//             return onError(exchange, "Empty JWT Token", HttpStatus.UNAUTHORIZED);
//         }

//         try {
//             if (!jwtUtil.isTokenValid(token)) {
//                 return onError(exchange, "Invalid or Expired Token", HttpStatus.UNAUTHORIZED);
//             }
            
//             // Extract user information and add to headers for downstream services
//             String userId = jwtUtil.extractUserId(token);
//             ServerHttpRequest modifiedRequest = request.mutate()
//                     .header("X-User-Id", userId)
//                     .build();
            
//             return chain.filter(exchange.mutate().request(modifiedRequest).build());
            
//         } catch (Exception e) {
//             return onError(exchange, "Token Processing Error: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
//         }
//     }

//     private boolean isPublicPath(String path) {
//         return path.startsWith("/actuator/") || 
//                path.equals("/health") ||
//                path.startsWith("/api/auth/");
//     }

//     private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
//         ServerHttpResponse response = exchange.getResponse();
//         response.setStatusCode(status);
//         response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        
//         String errorResponse = String.format("{\"error\": \"%s\", \"status\": %d}", 
//                                            message, status.value());
        
//         DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes());
//         return response.writeWith(Mono.just(buffer));
//     }

//     @Override
//     public int getOrder() {
//         return -1;
//     }
// }

package com.streamit.api_gateway_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.core.io.buffer.DataBuffer;

import com.streamit.api_gateway_service.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;
        
    // Use constructor injection instead of @RequiredArgsConstructor
    @Autowired
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private static final String AUTH_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
                
        // Skip authentication for specific paths (health check, auth, and public user endpoints)
        if (isPublicPath(request.getPath().toString())) {
            return chain.filter(exchange);
        }

        if (!request.getHeaders().containsKey(AUTH_HEADER)) {
            return onError(exchange, "Missing Authorization Header", HttpStatus.UNAUTHORIZED);
        }

        String authHeader = request.getHeaders().getFirst(AUTH_HEADER);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return onError(exchange, "Invalid Authorization Header Format", HttpStatus.UNAUTHORIZED);
        }

        String token = authHeader.substring(TOKEN_PREFIX.length()).trim();
                
        if (token.isEmpty()) {
            return onError(exchange, "Empty JWT Token", HttpStatus.UNAUTHORIZED);
        }

        try {
            if (!jwtUtil.isTokenValid(token)) {
                return onError(exchange, "Invalid or Expired Token", HttpStatus.UNAUTHORIZED);
            }
                        
            // Extract user information and add to headers for downstream services
            String userId = jwtUtil.extractUserId(token);
            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .build();
                        
            return chain.filter(exchange.mutate().request(modifiedRequest).build());
                    
        } catch (Exception e) {
            return onError(exchange, "Token Processing Error: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/actuator/") ||
                path.equals("/health") ||
                path.startsWith("/api/auth/") ||
                path.equals("/api/user/register") ||
                path.equals("/api/user/login") ||
                path.startsWith("/api/user/register") ||
                path.startsWith("/api/user/login");
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                
        String errorResponse = String.format("{\"error\": \"%s\", \"status\": %d}",
                                            message, status.value());
                
        DataBuffer buffer = response.bufferFactory().wrap(errorResponse.getBytes());
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -1;
    }
}