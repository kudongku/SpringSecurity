package com.example.springsecurity.domain.user.service;

import com.example.springsecurity.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtUtil jwtUtil;

    public String login(
        String username,
        String password,
        HttpServletResponse response
    ) {
        // 인증 과정 생략
        String bearerToken = jwtUtil.createJwt(username);
        response.addHeader(HttpHeaders.AUTHORIZATION, bearerToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        return bearerToken;
    }
}
