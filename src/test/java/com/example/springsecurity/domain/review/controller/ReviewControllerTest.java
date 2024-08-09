package com.example.springsecurity.domain.review.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.springsecurity.domain.common.MockSpringSecurityFilter;
import com.example.springsecurity.domain.common.UserFixture;
import com.example.springsecurity.global.config.AuthenticationConfig;
import java.security.Principal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@WebMvcTest(
    controllers = ReviewController.class,
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.ASSIGNABLE_TYPE,
            classes = AuthenticationConfig.class
        )
    }
)
class ReviewControllerTest implements UserFixture {

    @Autowired
    private MockMvc mockMvc;

    private Principal mockPrincipal;
    private Principal mockPrincipalUser;

    @Autowired
    private WebApplicationContext context;


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
    void createReview_withUserRole_shouldReturnSuccess() throws Exception {
        mockMvc.perform(
                post("/api/v1/reviews")
                    .principal(mockPrincipal)
            )
            .andExpect(status().isOk())
            .andExpect(content().string("testUserUsername님의 리뷰 등록이 완료되었습니다."))
            .andDo(print());
    }

    @Test
    void deleteReview_withAdminRole_shouldReturnSuccess() throws Exception {
        mockMvc.perform(
                delete("/api/v1/reviews")
                    .principal(mockPrincipal)
            )
            .andExpect(status().isOk())
            .andExpect(content().string("testUserUsername님이 리뷰를 삭제했습니다."));
    }

    @Test
    void deleteReview_withUserRole_shouldReturnAccessDenied() throws Exception {
        mockMvc.perform(
                delete("/api/v1/reviews")
                    .principal(mockPrincipalUser)
            )
            .andExpect(status().isForbidden());
    }

}
