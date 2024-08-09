package com.example.springsecurity.domain.user.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springsecurity.domain.common.MockSpringSecurityFilter;
import com.example.springsecurity.domain.common.UserFixture;
import com.example.springsecurity.domain.user.dto.UserGiveAuthorityRequestDto;
import com.example.springsecurity.domain.user.dto.UserLoginRequestDto;
import com.example.springsecurity.domain.user.dto.UserSignupRequestDto;
import com.example.springsecurity.domain.user.service.UserService;
import com.example.springsecurity.global.config.AuthenticationConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = AuthenticationConfig.class
        )
    }
)
class UserControllerTest implements UserFixture {

    @Autowired
    private MockMvc mockMvc;

    private Principal mockPrincipal;
    private Principal mockPrincipalUser;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply(springSecurity(new MockSpringSecurityFilter()))
            .build();
        this.mockUserSetup();
    }

    private void mockUserSetup() {
        mockPrincipal = new UsernamePasswordAuthenticationToken(
            TEST_USER_USERNAME,
            null,
            AUTHORITIES_ADMIN
        );
        mockPrincipalUser = new UsernamePasswordAuthenticationToken(
            TEST_USER_USERNAME,
            null,
            AUTHORITIES_USER
        );
    }

    @Test
    void testSignup() throws Exception {
        UserSignupRequestDto signupRequest = new UserSignupRequestDto("user1", "password",
            "nickname1");

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest))
            )
            .andExpect(status().isOk())
            .andExpect(content().string("회원가입이 완료되었습니다."));
    }

    @Test
    void testLogin() throws Exception {
        UserLoginRequestDto loginRequest = new UserLoginRequestDto("user1", "password");

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
            )
            .andExpect(status().isOk());
    }

    @Test
    void testLogout() throws Exception {
        mockMvc.perform(get("/api/v1/users/logout")
            )
            .andExpect(status().isOk())
            .andExpect(content().string("로그아웃이 완료되었습니다."));
    }


    @Test
    void testRefreshToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/refresh-token")
            )
            .andExpect(status().isOk());
    }

    @Test
    void testGiveAuthorityToUser_WithAdminRole() throws Exception {
        UserGiveAuthorityRequestDto requestDto = new UserGiveAuthorityRequestDto(1L, "user1");

        mockMvc.perform(post("/api/v1/admin")
                .principal(mockPrincipal)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isOk())
            .andExpect(content().string("user1님에게 관리자 권한을 업데이트 했습니다."))
            .andDo(print());
    }

    @Test
    void testGiveAuthorityToUser_WithoutAdminRole() throws Exception {
        UserGiveAuthorityRequestDto requestDto = new UserGiveAuthorityRequestDto(1L, "user1");

        mockMvc.perform(post("/api/v1/admin")
                .principal(mockPrincipalUser)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto))
            )
            .andExpect(status().isForbidden());
    }

}