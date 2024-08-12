package com.example.springsecurity.domain.user.service;

import com.example.springsecurity.domain.user.entity.AuthorityEnum;
import com.example.springsecurity.domain.user.entity.User;
import com.example.springsecurity.domain.user.entity.UserTokenInfo;
import com.example.springsecurity.domain.user.repository.UserRepository;
import com.example.springsecurity.domain.user.repository.UserTokenInfoRepository;
import com.example.springsecurity.global.exception.ExpiredTokenException;
import com.example.springsecurity.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final static Long ACCESS_TOKEN_EXPIRED_MS = 1000 * 60 * 30L; // 0.5 hour
    private final static Long REFRESH_TOKEN_EXPIRED_MS = 1000 * 60 * 60 * 24 * 7L; // 1 day

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserTokenInfoRepository userTokenInfoRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
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

    @Transactional
    public String login(
        String username,
        String password,
        HttpServletResponse response
    ) {
        User user = userRepository.findByUsername(username).orElseThrow(
            () -> new IllegalArgumentException("잘못된 아이디를 입력했습니다.")
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호를 입력했습니다.");
        }

        String accessToken = jwtUtil.createJwt(
            username,
            user.getAuthorities(),
            ACCESS_TOKEN_EXPIRED_MS
        );

        String refreshToken = jwtUtil.createJwt(
            username,
            user.getAuthorities(),
            REFRESH_TOKEN_EXPIRED_MS
        );

        UserTokenInfo userTokenInfo = userTokenInfoRepository.findByUser(user)
            .orElse(new UserTokenInfo(user));

        userTokenInfo.update(accessToken, refreshToken);
        userTokenInfoRepository.save(userTokenInfo);

        response.addHeader(HttpHeaders.AUTHORIZATION, accessToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        return accessToken;
    }

    @Transactional
    public void logout(HttpServletRequest request) {
        String token = jwtUtil.getJwtFromHeader(request);

        if (token == null) {
            throw new ExpiredTokenException("토큰이 존재하지 않습니다.");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        User user = userRepository.findByUsername(username).orElseThrow(
            () -> new NullPointerException("잘못된 아이디를 입력했습니다.")
        );

        userTokenInfoRepository.deleteByUser(user);
    }

    @Transactional
    public String refreshToken(
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        String bearerToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        UserTokenInfo userTokenInfo = userTokenInfoRepository
            .findByAccessToken(bearerToken)
            .orElseThrow(
                () -> new NullPointerException("리프레시 토큰이 존재하지 않습니다.")
            );
        String refreshToken = userTokenInfo.getRefreshToken().substring(7);

        if (jwtUtil.isExpired(refreshToken)) {
            throw new ExpiredTokenException("리프래시 토큰이 만료되었습니다.");
        }

        String accessToken = jwtUtil.createJwt(
            userTokenInfo.getUser().getUsername(),
            userTokenInfo.getUser().getAuthorities(),
            ACCESS_TOKEN_EXPIRED_MS
        );
        userTokenInfo.refresh(accessToken);

        response.addHeader(HttpHeaders.AUTHORIZATION, accessToken);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        return accessToken;
    }

    @Transactional
    public void giveAuthorityToUser(Long userId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
            () -> new IllegalArgumentException("잘못된 아이디를 입력했습니다.")
        );

        if (!Objects.equals(user.getId(), userId)) {
            throw new IllegalArgumentException("userId가 일치하지 않습니다.");
        }

        if (user.getAuthorities().contains(AuthorityEnum.ADMIN)) {
            throw new IllegalArgumentException("이미 권한을 가지고 있습니다.");
        }

        user.updateAuthority();
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
