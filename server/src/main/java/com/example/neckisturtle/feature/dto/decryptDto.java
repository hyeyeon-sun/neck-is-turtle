package com.example.neckisturtle.feature.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class decryptDto {

    private String encrypted;

    @Builder
    public decryptDto(String encrypted) {
        this.encrypted = encrypted;
    }
}
