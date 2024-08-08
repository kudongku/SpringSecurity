package com.example.springsecurity.global.filter;

import com.example.springsecurity.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j(topic = "JwtFilter")
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request, // null pointer exception 방지
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();

        // 로그인 및 회원가입 경로에서는 필터를 건너뛰기
        if (path.equals("/api/v1/users/signup") || path.equals("/api/v1/users/login") || path.equals("/api/v1/users/refreshToken")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.getJwtFromHeader(request);

        if (token == null) {
            log.error("토큰이 유효하지 않습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // token이 만료되었는지 확인하기
        if (jwtUtil.isExpired(token)) {
            log.error("토큰이 만료되었습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        // username token 에서 꺼내기
        String username = jwtUtil.getUsernameFromToken(token);
        List<SimpleGrantedAuthority> authorities = jwtUtil.getAuthoritiesFromToken(token);

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            username,
            null,
            authorities
        );

        // Detail을 넣는 단계
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}
