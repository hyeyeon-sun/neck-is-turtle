package com.example.neckisturtle.feature.persistance;

import com.example.neckisturtle.feature.domain.Mission;
import com.example.neckisturtle.feature.domain.MissionRecord;
import com.example.neckisturtle.feature.domain.Pose;
import com.example.neckisturtle.feature.domain.User;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MissionRecordRepo extends JpaRepository<MissionRecord, Integer>{
    @Override
    List<MissionRecord> findAll();
    List<MissionRecord> findAllByCompleteDtm(Date completeDate);
    List<MissionRecord> findAllByCompleteDtmAndUserId(Date completeDate, User userId);

    List<MissionRecord> findAllByUserId(User userId);

    Optional<MissionRecord> findById(Integer Id);

    Optional<MissionRecord> findByMissionIdAndUserId(Mission missionId, User userId);

    List<MissionRecord> findByCompleteDtmBetweenOrderByRegDtm(Date start, Date end);

    List<MissionRecord> findByCompleteDtmBetweenAndUserIdOrderByRegDtm(Date start, Date end, User user);

    boolean existsByCompleteDtmAndUserIdAndMissionId(Date date, User user, Mission mission);
}
