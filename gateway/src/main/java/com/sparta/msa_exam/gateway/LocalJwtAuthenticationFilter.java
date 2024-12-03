package com.sparta.msa_exam.gateway;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalJwtAuthenticationFilter implements GlobalFilter {

    @Value("${service.jwt.issuer}")
    private String TOKEN_ISSUER;

    @Value("${service.jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${spring.cloud.gateway.routes[2].uri}")
    private String AUTH_SERVICE_URL;

    private final WebClient.Builder webClientBuilder;

    private SecretKey secretKey;

    @Value("${public.paths}")
    private List<String> paths;

    private Set<String> PUBLIC_PATHS;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(SECRET_KEY));
        PUBLIC_PATHS = paths == null ? Set.of() : new HashSet<>(paths);
        log.info("Initialized PUBLIC_PATHS: {}", PUBLIC_PATHS);
    }

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (PUBLIC_PATHS.contains(path)) {
            log.info("공개된 endpoint: {}, JWT 검증을 생략합니다.", path);
            return chain.filter(exchange);
        }

        return extractToken(exchange)
            .flatMap(token -> {
                try {
                    Claims claims = validateToken(token, exchange);

                    // TODO: OAuth2 규칙 검증 추가
                    if (!TOKEN_ISSUER.equals(claims.getIssuer())) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 token issuer 입니다.");
                    }
                    if (claims.getExpiration().before(new Date())) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token이 만료되었습니다.");
                    }

                    String stringUserId = claims.get("userId", String.class);
                    if (stringUserId == null) {
                        return onError(exchange, "Invalid user", HttpStatus.UNAUTHORIZED);
                    }

                    Long userId = Long.parseLong(stringUserId);

                    return isValidUser(userId).flatMap(isValid -> {
                        if (!isValid) {
                            return onError(exchange, "Invalid user", HttpStatus.UNAUTHORIZED);
                        }
                        return chain.filter(exchange);
                    });
                } catch (Exception e) {
                    return onError(exchange, "Invalid user", HttpStatus.UNAUTHORIZED);
                }
            });
    }

    private Mono<Boolean> isValidUser(Long userId) {
        return getUser(userId)
            .map(userReponseDTO -> true)
            .onErrorResume(e -> Mono.just(false));
    }

    private Mono<UserResponseDTO> getUser(Long userId) {
        return webClientBuilder.baseUrl(AUTH_SERVICE_URL)
            .build()
            .method(HttpMethod.GET)
            .uri("/api/auth/{userId}", userId)
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), response -> {
                return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "존재하지 않는 사용자입니다."));
            })
            .onStatus(status -> status.is5xxServerError(), response -> {
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "서버 오류가 발생했습니다."));
            })
            .bodyToMono(UserResponseDTO.class);
    }

    private Mono<String> extractToken(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(HEADER);
        if (authHeader == null || !authHeader.startsWith(TOKEN_PREFIX)) {
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "Authorization header가 누락되었거나 유효하지 않습니다."));
        }
        return Mono.just(authHeader.replace(TOKEN_PREFIX, ""));
    }

    private Mono<Void> onError(ServerWebExchange exchange, String error, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        return response.setComplete();
    }

    private Claims validateToken(String token, ServerWebExchange exchange) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

            log.info("##### Claims: " + claims.toString());

            exchange.getRequest().mutate()
                .header("X-User-Id", claims.getSubject())
                .header("X-Role", claims.get("role").toString())
                .build();

            return claims;
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }
}
