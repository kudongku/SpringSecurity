package com.example.springsecurity.global.exception;

import org.springframework.security.access.AccessDeniedException;

public class ExpiredTokenException extends AccessDeniedException {

    public ExpiredTokenException(String msg) {
        super(msg);
    }

}
