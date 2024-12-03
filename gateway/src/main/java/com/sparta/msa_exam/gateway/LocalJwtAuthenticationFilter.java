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

    // TODO : 검토하기
//    @Value("${spring.cloud.gateway.routes[2].uri}")
    private String AUTH_SERVICE_URL = "http://localhost:19095";

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


                    log.info("Token: {}", claims);
                    log.info("Request 헤더: {}", exchange.getRequest().getHeaders());

                    // TODO: OAuth2 규칙 검증 추가
                    if (!TOKEN_ISSUER.equals(claims.getIssuer())) {
                        log.info("유효하지 않은 token issuer actual: {} expected: {}", claims.getIssuer(), TOKEN_ISSUER);
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "유효하지 않은 token issuer 입니다.");
                    }
                    if (claims.getExpiration().before(new Date())) {
                        log.info("만료된 토큰");
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token이 만료되었습니다.");
                    }

                    String stringUserId = claims.getSubject();
                    if (stringUserId == null) {
                        return onError(exchange, "Invalid user 1", HttpStatus.UNAUTHORIZED);
                    }

                    Long userId = Long.parseLong(stringUserId);

                    log.info("UserID: {}", userId);

                    return isValidUser(userId)
                        .doOnNext(isValid -> log.info("User {} is valid: {}", userId, isValid))
                        .flatMap(isValid -> {
                            if (!isValid) {
                                log.info("Invalid user: {}", userId);
                                return onError(exchange, "Invalid user 2", HttpStatus.UNAUTHORIZED);
                            }
                            return chain.filter(exchange);
                        });
                } catch (Exception e) {
                    log.info("Exception 예외처리");
                    return onError(exchange, "Invalid user 3", HttpStatus.UNAUTHORIZED);
                }
            });
    }

    private Mono<Boolean> isValidUser(Long userId) {
        return getUser(userId)
            .doOnNext(userResponseDTO -> log.info("User retrieved successfully: {}", userResponseDTO))
            .map(userResponseDTO -> true)
            .onErrorResume(e -> {
                log.warn("Error occurred while retrieving user with ID {}: {}", userId, e.getMessage());
                return Mono.just(false);
            });
    }

    private Mono<UserResponseDTO> getUser(Long userId) {
        log.info("Attempting to fetch user with ID {}", userId);
        return webClientBuilder.baseUrl(AUTH_SERVICE_URL)
            .build()
            .method(HttpMethod.GET)
            .uri(uriBuilder -> uriBuilder.path("/api/auth/{userId}").build(userId))
            .retrieve()
            .onStatus(status -> status.is4xxClientError(), response -> {
                log.warn("4xx error while fetching user with ID {}: {}", userId, response.statusCode());
                return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."));
            })
            .onStatus(status -> status.is5xxServerError(), response -> {
                log.error("5xx error while fetching user with ID {}: {}", userId, response.statusCode());
                return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다."));
            })
            .bodyToMono(UserResponseDTO.class)
            .doOnSuccess(userResponseDTO -> log.info("Successfully fetched user: {}", userResponseDTO))
            .doOnError(e -> log.error("Failed to fetch user with ID {}: {}", userId, e.getMessage()));
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
        log.error("Error occurred: {}, Status: {}", error, status);
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
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
