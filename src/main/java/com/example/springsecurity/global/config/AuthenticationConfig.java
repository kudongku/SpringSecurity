package com.example.springsecurity.global.config;

import com.example.springsecurity.global.filter.JwtFilter;
import com.example.springsecurity.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class AuthenticationConfig {

    private final JwtUtil jwtUtil;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf(AbstractHttpConfigurer::disable)
            // Cross site Request forgery, rest api를 이용한 서버에서는 stateless하기 때문에 서버에 인증정보를 저장하지 않아 필요하지 않다.
            .sessionManagement(
                (sessionManagement) -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(
                (authorize) -> authorize.requestMatchers(
                    "/api/v1/users/**",
                    "/v3/api-docs/**",       // OpenAPI 3 문서 관련 엔드포인트
                    "/swagger-ui/**",        // Swagger UI 관련 엔드포인트
                    "/swagger-ui.html",      // Swagger UI 메인 페이지
                    "/swagger-resources/**", // Swagger 리소스
                    "/webjars/**",           // 웹자바 리소스
                    "/configuration/ui",     // Swagger UI 설정
                    "/configuration/security"// Swagger 보안 설정
                ).permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new JwtFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }

}
