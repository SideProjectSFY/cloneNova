package com.ssafy.clonenova.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponseDto {

    private String userId;
    private String email;
    private String name;
    private String nickname;
    private String provider;
    private LocalDateTime createdAt;
}