package com.example.springsecurity.domain.user.service;

import com.example.springsecurity.global.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    private final Long expiredMs = 1000 * 60 * 60 * 24L; // 1 day

    public String login(String username, String password) {
        // 인증 과정 생략
        return JwtUtil.createJwt(username, secretKey, expiredMs);
    }
}
