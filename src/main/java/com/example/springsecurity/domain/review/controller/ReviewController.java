package com.example.springsecurity.domain.review.controller;

import com.example.springsecurity.domain.user.entity.AuthorityEnum;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/reviews")
@RestController
public class ReviewController {

    @PostMapping
    public ResponseEntity<String> writeReview(
        Authentication authentication
    ) {
        return ResponseEntity.ok(authentication.getName() + "님의 리뷰 등록이 완료되었습니다.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteMapping(
        Authentication authentication
    ) {

        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(
            AuthorityEnum.ADMIN.getAuthorityName()
        );

        if (!authentication.getAuthorities().contains(adminAuthority)) {
            return ResponseEntity.status(403).body("권한이 없습니다.");
        }

        return ResponseEntity.ok(authentication.getName() + "님이 리뷰를 삭제했습니다.");
    }

}
