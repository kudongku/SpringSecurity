package com.example.springsecurity.domain.user.service;

import com.example.springsecurity.domain.user.entity.User;
import com.example.springsecurity.domain.user.repository.UserRepository;
import com.example.springsecurity.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(
        String username,
        String password,
        String nickname
    ) {
        validateUserName(username);
        validateNickname(nickname);
        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(username, encodedPassword, nickname);

        userRepository.save(user);
    }

    public String login(
        String username,
        String password,
        HttpServletResponse response
    ) {
        User user = userRepository.findByUsername(username).orElseThrow(
            () -> new RuntimeException("잘못된 아이디를 입력했습니다.")
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("잘못된 비밀번호를 입력했습니다.");
        }

        String bearerToken = jwtUtil.createJwt(username, user.getAuthorities());
        response.addHeader(HttpHeaders.AUTHORIZATION, bearerToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        return bearerToken;
    }

    private void validateNickname(String nickname) {

        if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("동일한 닉네임이 존재합니다.");
        }

    }

    private void validateUserName(String username) {

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("동일한 아이디가 존재합니다.");
        }

    }

}
