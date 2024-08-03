package com.example.springsecurity.domain.user.controller;

import com.example.springsecurity.domain.user.dto.UserGiveAuthorityRequestDto;
import com.example.springsecurity.domain.user.dto.UserLoginRequestDto;
import com.example.springsecurity.domain.user.dto.UserSignupRequestDto;
import com.example.springsecurity.domain.user.entity.AuthorityEnum;
import com.example.springsecurity.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("api/v1/users")
@Tag(name = "UserController", description = "회원가입, 로그인, 관리자 권한 업데이트가 가능한 컨트롤러입니다.")
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "회원가입시 유저를 생성하고 사용자 권한을 줍니다.")
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
    @Operation(summary = "로그인", description = "로그인시 response header에 token을 넣고, 출력합니다.")
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

    @PostMapping("/admin")
    @Operation(summary = "관리자 권한 부여", description = "관리자 권한을 가진 user가 다른 유저에게 관리자 권한을 부여합니다.")
    public ResponseEntity<String> giveAuthority(
        @RequestBody UserGiveAuthorityRequestDto userGiveAuthorityRequestDto,
        Authentication authentication
    ) {
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(
            AuthorityEnum.ADMIN.getAuthorityName()
        );

        if (!authentication.getAuthorities().contains(adminAuthority)) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        userService.giveAuthority(
            userGiveAuthorityRequestDto.getUserId(),
            userGiveAuthorityRequestDto.getUsername()
        );

        return ResponseEntity.ok(
            userGiveAuthorityRequestDto.getUsername() + "님에게 관리자 권한을 업데이트 했습니다."
        );
    }

}
