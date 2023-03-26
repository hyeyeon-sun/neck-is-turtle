package com.example.neckisturtle.feature.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class DayDto {

    private String day;

    @Builder
    public DayDto(String day) {
        this.day = day;
    }
}
