package com.example.springsecurity.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserGiveAuthorityRequestDto {

    private Long userId;
    private String username;

}
