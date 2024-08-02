package com.example.springsecurity.domain.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewController {

    @PostMapping()
    public ResponseEntity<String> writeReview(Authentication authentication) {
        return ResponseEntity.ok(authentication.getName() + "님의 리뷰 등록이 완료되었습니다.");
    }

}
