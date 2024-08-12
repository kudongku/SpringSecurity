package com.example.springsecurity.global.util;

import com.example.springsecurity.domain.user.entity.AuthorityEnum;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;
    private final String BEARER_PREFIX = "Bearer ";
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    public String createJwt(String username, List<AuthorityEnum> authorities, Long expiredMs) {
        List<String> authoritiesToString = authorities.stream()
            .map(AuthorityEnum::getAuthorityName)
            .toList();

        Claims claims = Jwts.claims(); // Map을 상속받는 객체 Claims
        claims.put("username", username);
        claims.put("authorities", authoritiesToString);

        return BEARER_PREFIX +
            Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.info("bearerToken : {}", bearerToken);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    public boolean isExpired(String token) {
        try {
            Date expirationDate = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
            Date now = new Date();
            return expirationDate.before(now);
        } catch (ExpiredJwtException e) {
            log.error("토큰이 만료되었습니다.");
            return true;
        } catch (Exception e) {
            log.error("Error parsing JWT: {}", e.getMessage());
            return true;
        }
    }


    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("username", String.class);
    }

    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        List<?> authorities = Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .get("authorities", List.class);

        return authorities.stream()
            .map(authority -> new SimpleGrantedAuthority((String) authority))
            .collect(Collectors.toList());
    }

}
