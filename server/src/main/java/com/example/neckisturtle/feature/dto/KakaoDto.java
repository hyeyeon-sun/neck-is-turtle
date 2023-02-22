package com.example.neckisturtle.feature.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class KakaoDto {

    private String accessToken;

    @Builder
    public KakaoDto(String access_token) {
        this.accessToken = access_token;
    }
}
