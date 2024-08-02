package com.example.springsecurity.domain.user.service;

import com.example.springsecurity.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtUtil jwtUtil;

    public String login(String username, String password) {
        // 인증 과정 생략
        return jwtUtil.createJwt(username);
    }
}
