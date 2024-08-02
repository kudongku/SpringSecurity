package com.example.springsecurity.domain.user.entity;

import lombok.Getter;

@Getter
public enum AuthorityEnum {
    USER(Authority.USER),
    ADMIN(Authority.ADMIN);

    private final String authorityName;

    AuthorityEnum(String authorityName) {
        this.authorityName = authorityName;
    }

    public static class Authority {
        public static final String USER = "ROLE_USER";
        public static final String ADMIN = "ROLE_ADMIN";
    }
}
