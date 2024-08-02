package com.example.springsecurity.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtUtil {

    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public static String createJwt(String username, String secretKey, Long expireMs) {
        Claims claims = Jwts.claims(); // Map을 상속받는 객체 Claims
        claims.put("username", username);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expireMs))
            .signWith(key)
            .compact();
    }
}
