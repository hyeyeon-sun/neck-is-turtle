package com.example.neckisturtle.feature.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class MissionListDto {
    private Integer missionId;
    private Date complateDtm;

    @Builder
    public MissionListDto(Integer mission, Date date) {
        this.missionId = mission;
        this.complateDtm = date;
    }
}
