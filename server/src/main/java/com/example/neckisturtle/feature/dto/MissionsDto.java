package com.example.neckisturtle.feature.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Getter
@NoArgsConstructor
public class MissionsDto {

    String day;
    Integer value;

    @Builder
    public MissionsDto(String date, Integer value) {
        this.day = date;
        this.value = value;
    }
}
