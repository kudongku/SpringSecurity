package com.example.springsecurity.domain.user.controller;

import com.example.springsecurity.domain.user.dto.UserLoginRequestDto;
import com.example.springsecurity.domain.user.dto.UserSignupRequestDto;
import com.example.springsecurity.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(
        @RequestBody UserSignupRequestDto usersignupRequestDto
    ) {
        userService.signup(
            usersignupRequestDto.getUsername(),
            usersignupRequestDto.getPassword(),
            usersignupRequestDto.getNickname()
        );

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
        @RequestBody UserLoginRequestDto userLoginRequestDto,
        HttpServletResponse response
    ) {
        String bearerToken = userService.login(
            userLoginRequestDto.getUsername(),
            userLoginRequestDto.getPassword(),
            response
        );

        return ResponseEntity.ok(bearerToken);
    }

}
