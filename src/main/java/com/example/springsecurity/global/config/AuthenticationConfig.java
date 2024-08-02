package com.example.springsecurity.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@Configuration
public class AuthenticationConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf(AbstractHttpConfigurer::disable)
            // Cross site Request forgery, rest api를 이용한 서버에서는 stateless하기 때문에 서버에 인증정보를 저장하지 않아 필요하지 않다.
            .authorizeHttpRequests((authorize) -> authorize
                .requestMatchers("/api/v1/users/signup", "/api/v1/users/login").permitAll()
                .anyRequest().authenticated()
            );
        // @formatter:on
        return http.build();
    }

}
