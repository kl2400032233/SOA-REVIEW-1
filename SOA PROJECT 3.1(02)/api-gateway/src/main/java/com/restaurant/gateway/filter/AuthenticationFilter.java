package com.restaurant.gateway.filter;

import com.restaurant.gateway.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Global Authentication and Role-Based Authorization Filter at API Gateway.
 * Intercepts all incoming client traffic:
 * 1. Checks if the endpoint is public.
 * 2. If protected, validates JWT from Bearer token (rejects with 401 if missing/invalid).
 * 3. Enforces Role-Based Access Control (RBAC):
 *    - Menu mutations (POST, PUT, DELETE, PATCH /api/menu/**) require ADMIN role.
 *    - Order status modification (PUT /api/orders/{id}/status) requires STAFF role.
 *    - All-orders retrieval (GET /api/orders) requires STAFF or ADMIN role.
 * 4. Injects user context (X-User-Id, X-User-Role, X-User-Email) into downstream requests.
 */
@Component
public class AuthenticationFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    private final JwtUtil jwtUtil;

    // Public endpoints that do not require JWT authentication
    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/api/auth/register",
            "/api/auth/login",
            "/actuator"
    );

    public AuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        HttpMethod method = request.getMethod();

        // 1. Bypass public authentication endpoints
        if (isPublicEndpoint(path)) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // Allow public menu browsing when token is omitted
        boolean isPublicMenuBrowsing = (path.equals("/api/menu") || path.equals("/api/menu/")) && method == HttpMethod.GET;
        if (isPublicMenuBrowsing && (authHeader == null || authHeader.isBlank())) {
            return chain.filter(exchange);
        }

        // 2. Validate Authorization header
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Unauthorized request to {}: Missing or invalid Authorization header", path);
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing or malformed Authorization header. Bearer token required.");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            logger.warn("Unauthorized request to {}: Invalid or expired JWT token", path);
            return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid or expired JWT token");
        }

        // 3. Extract identity and role claims
        Long userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);
        String email = jwtUtil.getEmail(token);

        logger.info("Request [{}] {} authenticated for user: {} (role: {})", method, path, email, role);

        // 4. Enforce Role-Based Access Control (RBAC)
        if (path.startsWith("/api/menu") && (method == HttpMethod.POST || method == HttpMethod.PUT ||
                method == HttpMethod.DELETE || method == HttpMethod.PATCH)) {
            if (!"ADMIN".equalsIgnoreCase(role)) {
                logger.warn("Forbidden access to {} by user {} with role {}", path, email, role);
                return onError(exchange, HttpStatus.FORBIDDEN, "Access denied: Admin role required for menu management");
            }
        }

        // Staff-only check for order status updates
        if (path.matches("^/api/orders/\\d+/status$") && method == HttpMethod.PUT) {
            if (!"STAFF".equalsIgnoreCase(role)) {
                logger.warn("Forbidden access to update order status by user {} with role {}", email, role);
                return onError(exchange, HttpStatus.FORBIDDEN, "Access denied: Staff role required to update order status");
            }
        }

        // Viewing all orders check (CUSTOMER must not view all orders)
        if ((path.equals("/api/orders") || path.equals("/api/orders/")) && method == HttpMethod.GET) {
            if ("CUSTOMER".equalsIgnoreCase(role)) {
                logger.warn("Forbidden access to view all orders by customer {}", email);
                return onError(exchange, HttpStatus.FORBIDDEN, "Access denied: Customers are not permitted to view all orders");
            }
        }

        // 5. Forward request to downstream microservice with enriched user headers
        ServerHttpRequest mutatedRequest = request.mutate()
                .header("X-User-Id", String.valueOf(userId))
                .header("X-User-Role", role)
                .header("X-User-Email", email)
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isPublicEndpoint(String path) {
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    private Mono<Void> onError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String path = exchange.getRequest().getURI().getPath();
        String jsonError = String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path
        );

        DataBuffer buffer = response.bufferFactory().wrap(jsonError.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }

    @Override
    public int getOrder() {
        return -100; // Run early in the filter chain
    }
}
