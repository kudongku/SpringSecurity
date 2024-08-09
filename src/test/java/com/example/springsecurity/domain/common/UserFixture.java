package com.example.springsecurity.domain.common;

import com.example.springsecurity.domain.user.entity.AuthorityEnum;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public interface UserFixture {
    String TEST_USER_USERNAME = "testUserUsername";

    SimpleGrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority(
        AuthorityEnum.ADMIN.getAuthorityName()
    );
    SimpleGrantedAuthority USER_AUTHORITY = new SimpleGrantedAuthority(
        AuthorityEnum.USER.getAuthorityName()
    );

    List<SimpleGrantedAuthority> AUTHORITIES_ADMIN = List.of(ADMIN_AUTHORITY);
    List<SimpleGrantedAuthority> AUTHORITIES_USER = List.of(USER_AUTHORITY);
}
