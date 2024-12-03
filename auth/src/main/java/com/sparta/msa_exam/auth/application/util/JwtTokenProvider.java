package com.sparta.msa_exam.auth.application.util;

import com.sparta.msa_exam.auth.application.domain.User;
import com.sparta.msa_exam.auth.framework.web.dto.TokenDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${spring.application.name}")
    private String ISSUER;

    @Value("${service.jwt.access-expiration}")
    private long ACCESS_EXPIRATION;

    @Value("${service.jwt.refresh-expiration}")
    private long REFRESH_EXPIRATION;

    @Value("${service.jwt.secret-key}")
    private String SECRET_KEY;

    private SecretKey secretKey;
    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(SECRET_KEY));
    }

    private final RedisTemplate<String, String> redisTemplate;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";
    public static final String REFRESH_TOKEN_PREFIX = "refreshToken:";

    public TokenDTO issueTokens(User user) {
        String access = generateAccessToken(user);
        String refresh = generateRefreshToken(user);

        redisTemplate.opsForValue().set(
            REFRESH_TOKEN_PREFIX + user.getId(),
            refresh,
            REFRESH_EXPIRATION,
            TimeUnit.MILLISECONDS
        );

        return new TokenDTO(access, refresh);
    }

    private String generateAccessToken(User user) {
        String jwt = Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("role", user.getUserRole())
            .claim("type", TYPE_ACCESS)
            .setIssuer(ISSUER)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();

        return TOKEN_PREFIX + jwt;
    }

    private String generateRefreshToken(User user) {
        return Jwts.builder()
            .setSubject(user.getId().toString())
            .claim("role", user.getUserRole())
            .claim("type", TYPE_REFRESH)
            .setIssuer(ISSUER)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact();
    }

    public boolean validateRefreshToken(String refreshToken) {
        String userId = extractUserId(refreshToken);
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
        return storedToken != null && storedToken.equals(refreshToken);
    }

    public String extractUserId(String token) {
        JwtParser jwtParser = Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build();

        Claims claims = jwtParser
            .parseClaimsJws(token.replace("Bearer ", ""))
            .getBody();

        return claims.getSubject();
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true) // javascript 접근 방지
//                .secure(true) // https 통신 강제
//                .sameSite("None")
                .maxAge(REFRESH_EXPIRATION)
                .build();
    }
}
