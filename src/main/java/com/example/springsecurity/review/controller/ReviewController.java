package com.example.springsecurity.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewController {

    @PostMapping()
    public ResponseEntity<String> writeReview() {
        return ResponseEntity.ok("리뷰 등록이 완료되었습니다.");
    }

}
