package com.example.springsecurity.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginRequestDto {

    @NotNull(message = "User ID cannot be null")
    private String username;

    @NotNull(message = "password cannot be null")
    private String password;

}
