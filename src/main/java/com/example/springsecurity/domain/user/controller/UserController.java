package com.example.springsecurity.domain.user.controller;

import com.example.springsecurity.domain.user.dto.UserLoginRequestDto;
import com.example.springsecurity.domain.user.service.UserService;
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

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        String bearerToken = userService.login(
            userLoginRequestDto.getUsername(),
            userLoginRequestDto.getPassword()
        );

        return ResponseEntity.ok(bearerToken);
    }

}
