package com.example.springsecurity.domain.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.example.springsecurity.domain.user.entity.User;
import com.example.springsecurity.domain.user.entity.UserTokenInfo;
import com.example.springsecurity.domain.user.repository.UserRepository;
import com.example.springsecurity.domain.user.repository.UserTokenInfoRepository;
import com.example.springsecurity.global.exception.ExpiredTokenException;
import com.example.springsecurity.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserTokenInfoRepository userTokenInfoRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @Nested
    @DisplayName("회원가입 테스트")
    class Signup {

        @Test
        @DisplayName("회원가입 성공")
        void signupSuccess() {
            // given
            given(userRepository.existsByUsername("testUser")).willReturn(false);
            given(userRepository.existsByNickname("testNick")).willReturn(false);
            given(passwordEncoder.encode("testPassword")).willReturn("encodedPassword");

            // when & then
            assertDoesNotThrow(() -> userService.signup("testUser", "testPassword", "testNick"));
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("중복된 아이디로 인한 실패")
        void signupFailureDuplicateUsername() {
            // given
            given(userRepository.existsByUsername("testUser")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signup("testUser", "testPassword", "testNick"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("동일한 아이디가 존재합니다.");
        }

        @Test
        @DisplayName("중복된 닉네임으로 인한 실패")
        void signupFailureDuplicateNickname() {
            // given
            given(userRepository.existsByNickname("testNick")).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.signup("testUser", "testPassword", "testNick"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("동일한 닉네임이 존재합니다.");
        }
    }

    @Nested
    @DisplayName("로그인 테스트")
    class Login {

        @Test
        @DisplayName("로그인 성공")
        void loginSuccess() {
            // given
            User mockUser = new User("testUser", "encodedPassword", "testNick");
            given(userRepository.findByUsername("testUser")).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches("testPassword", "encodedPassword")).willReturn(true);
            given(jwtUtil.createJwt("testUser", mockUser.getAuthorities(), 1800000L)).willReturn(
                "mockAccessToken");

            HttpServletResponse response = mock(HttpServletResponse.class);

            // when & then
            assertDoesNotThrow(() -> userService.login("testUser", "testPassword", response));
        }

        @Test
        @DisplayName("잘못된 아이디로 인한 로그인 실패")
        void loginFailureWrongUsername() {
            // given
            given(userRepository.findByUsername("wrongUser")).willReturn(Optional.empty());

            HttpServletResponse response = mock(HttpServletResponse.class);

            // when & then
            assertThatThrownBy(() -> userService.login("wrongUser", "testPassword", response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 아이디를 입력했습니다.");
        }

        @Test
        @DisplayName("잘못된 비밀번호로 인한 로그인 실패")
        void loginFailureWrongPassword() {
            // given
            User mockUser = new User("testUser", "encodedPassword", "testNick");
            given(userRepository.findByUsername("testUser")).willReturn(Optional.of(mockUser));
            given(passwordEncoder.matches("wrongPassword", "encodedPassword")).willReturn(false);

            HttpServletResponse response = mock(HttpServletResponse.class);

            // when & then
            assertThatThrownBy(() -> userService.login("testUser", "wrongPassword", response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 비밀번호를 입력했습니다.");
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class Logout {

        @Test
        @DisplayName("로그아웃 성공")
        void logoutSuccess() {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);
            User mockUser = new User("testUser", "encodedPassword", "testNick");

            given(jwtUtil.getJwtFromHeader(request)).willReturn("mockToken");
            given(jwtUtil.getUsernameFromToken("mockToken")).willReturn("testUser");
            given(userRepository.findByUsername("testUser")).willReturn(Optional.of(mockUser));

            // when & then
            assertDoesNotThrow(() -> userService.logout(request));
            verify(userTokenInfoRepository, times(1)).deleteByUser(mockUser);
        }
        @Test
        @DisplayName("로그아웃 성공")
        void logoutFailureWrongUsername() {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);
            User mockUser = new User("testUser", "encodedPassword", "testNick");

            given(jwtUtil.getJwtFromHeader(request)).willReturn("mockToken");
            given(jwtUtil.getUsernameFromToken("mockToken")).willReturn("testUser");
            given(userRepository.findByUsername("testUser")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.logout(request))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("잘못된 아이디를 입력했습니다.");        }

        @Test
        @DisplayName("토큰이 없는 로그아웃 실패")
        void logoutFailureNoToken() {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);
            given(jwtUtil.getJwtFromHeader(request)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> userService.logout(request))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessageContaining("토큰이 존재하지 않습니다.");
        }
    }

    @Nested
    @DisplayName("리프레시 토큰 테스트")
    class RefreshToken {

        @Test
        @DisplayName("리프레시 토큰 성공")
        void refreshTokenSuccess() {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            UserTokenInfo mockTokenInfo = new UserTokenInfo(
                new User("asd", "asd", "asd")
            );
            mockTokenInfo.update(
                "AccessToken", "refreshTokenasdfaasdf"
            );

            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer mockToken");
            given(userTokenInfoRepository.findByAccessToken("Bearer mockToken")).willReturn(
                Optional.of(mockTokenInfo));
            given(jwtUtil.isExpired(any())).willReturn(false);
            given(jwtUtil.createJwt(any(), any(), any())).willReturn("newAccessToken");

            // when & then
            String token = userService.refreshToken(request, response);
            verify(response, times(1)).addHeader(HttpHeaders.AUTHORIZATION, "newAccessToken");
        }

        @Test
        @DisplayName("리프레시 토큰이 존재하지 않습니다")
        void refreshTokenFailureWrongBearerToken() {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            UserTokenInfo mockTokenInfo = new UserTokenInfo(
                new User("asd", "asd", "asd")
            );
            mockTokenInfo.update(
                "AccessToken", "refreshTokenasdfaasdf"
            );

            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer mockToken");
            given(userTokenInfoRepository.findByAccessToken("Bearer mockToken")).willReturn(
                Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.refreshToken(request, response))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("리프레시 토큰이 존재하지 않습니다.");
        }

        @Test
        @DisplayName("리프레시 토큰 만료로 인한 실패")
        void refreshTokenFailureExpired() {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);
            HttpServletResponse response = mock(HttpServletResponse.class);
            UserTokenInfo mockTokenInfo = new UserTokenInfo(
                new User("asd", "asd", "asd")
            );
            mockTokenInfo.update(
                "AccessToken", "refreshTokenasdfaasdf"
            );

            given(request.getHeader(HttpHeaders.AUTHORIZATION)).willReturn("Bearer mockToken");
            given(userTokenInfoRepository.findByAccessToken("Bearer mockToken")).willReturn(
                Optional.of(mockTokenInfo));
            given(jwtUtil.isExpired(any())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> userService.refreshToken(request, response))
                .isInstanceOf(ExpiredTokenException.class)
                .hasMessageContaining("리프래시 토큰이 만료되었습니다.");
        }
    }

    @Nested
    @DisplayName("권한 부여 테스트")
    class GiveAuthorityToUser {

        @Test
        @DisplayName("권한 부여 성공")
        void giveAuthorityToUserSuccess() {
            // given
            User mockUser = mock(User.class);  // Mockito로 mock 객체 생성
            given(userRepository.findByUsername("testUser")).willReturn(Optional.of(mockUser));
            given(mockUser.getId()).willReturn(1L);

            // when
            userService.giveAuthorityToUser(1L, "testUser");

            // then
            verify(mockUser, times(1)).updateAuthority();  // updateAuthority가 호출되었는지 확인
        }

        @Test
        @DisplayName("아이디가 일치하지 않아 권한 부여 실패")
        void giveAuthorityToUserFailureIdMismatch() {
            // given
            User mockUser = new User("testUser", "encodedPassword", "testNick");
            given(userRepository.findByUsername("testUser")).willReturn(Optional.of(mockUser));

            // when & then
            assertThatThrownBy(() -> userService.giveAuthorityToUser(999L, "testUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("userId가 일치하지 않습니다.");
        }

        @Test
        @DisplayName("잘못된 아이디를 입력했습니다")
        void giveAuthorityToUserFailureNull() {
            // given
            User mockUser = new User("testUser", "encodedPassword", "testNick");
            given(userRepository.findByUsername("testUser")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.giveAuthorityToUser(999L, "testUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 아이디를 입력했습니다.");
        }

        @Test
        @DisplayName("이미 권한이 있어 권한 부여 실패")
        void giveAuthorityToUserFailureAlreadyHasAuthority() {
            // given
            User mockUser = mock(User.class);
            given(userRepository.findByUsername("testUser")).willReturn(Optional.of(mockUser));
            given(mockUser.getAuthorities()).willReturn((List.of("ROLE_ADMIN")));

            // when & then
            assertThatThrownBy(() -> userService.giveAuthorityToUser(mockUser.getId(), "testUser"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 권한을 가지고 있습니다.");
        }
    }
}
