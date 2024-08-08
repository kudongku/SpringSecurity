package com.example.springsecurity.domain.review.controller;

import com.example.springsecurity.domain.user.entity.AuthorityEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/reviews")
@Tag(name = "ReviewController", description = "권한이 있어야 접근이 가능합니다.")
@RestController
public class ReviewController {

    @PostMapping
    @Operation(summary = "리뷰작성", description = "user권한이 있을경우 리뷰를 작성할 수 있습니다.")
    public ResponseEntity<String> createReview(
        Authentication authentication
    ) {
        return ResponseEntity.ok(authentication.getName() + "님의 리뷰 등록이 완료되었습니다.");
    }

    @DeleteMapping
    @Operation(summary = "리뷰삭제", description = "admin 권한이 있을 경우 리뷰를 삭제할 수 있습니다.")
    public ResponseEntity<String> deleteReview(
        Authentication authentication
    ) {
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority(
            AuthorityEnum.ADMIN.getAuthorityName()
        );

        if (!authentication.getAuthorities().contains(adminAuthority)) {
            throw new AccessDeniedException("권한이 없습니다.");
        }

        return ResponseEntity.ok(authentication.getName() + "님이 리뷰를 삭제했습니다.");
    }

}
