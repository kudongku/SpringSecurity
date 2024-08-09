package com.example.springsecurity.global.filter;

import com.example.springsecurity.global.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
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
        if (path.startsWith("/api/v1/users/") ||
            path.startsWith("/v3/api-docs") ||
            path.startsWith("/swagger-ui/") ||
            path.startsWith("/swagger-ui.html") ||
            path.startsWith("/swagger-resources/") ||
            path.startsWith("/webjars/") ||
            path.startsWith("/configuration/ui") ||
            path.startsWith("/configuration/security")
        ) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = jwtUtil.getJwtFromHeader(request);

        if (token == null) {
            String errorMessage = "토큰이 제공되지 않았습니다.";
            filterExceptionHandler(response, errorMessage);
            return;
        }

        try {
            String username = jwtUtil.getUsernameFromToken(token);
            List<SimpleGrantedAuthority> authorities = jwtUtil.getAuthoritiesFromToken(token);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                username,
                null,
                authorities
            );
            authenticationToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        } catch (SecurityException | MalformedJwtException e) {
            String errorMessage = "Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.";
            filterExceptionHandler(response, errorMessage);
            return;
        } catch (ExpiredJwtException e) {
            String errorMessage = "Expired JWT token, 만료된 JWT token 입니다.";
            filterExceptionHandler(response, errorMessage);
            return;
        } catch (UnsupportedJwtException e) {
            String errorMessage = "Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.";
            filterExceptionHandler(response, errorMessage);
            return;
        } catch (IllegalArgumentException e) {
            String errorMessage = "JWT claims is empty, 잘못된 JWT 토큰 입니다.";
            filterExceptionHandler(response, errorMessage);
            return;
        } catch (Exception e) {
            filterExceptionHandler(response, e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);

    }

    private void filterExceptionHandler(
        HttpServletResponse response,
        String errorMessage
    ) throws IOException {
        log.error(errorMessage);
        response.setStatus(403);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(errorMessage);
    }

}
