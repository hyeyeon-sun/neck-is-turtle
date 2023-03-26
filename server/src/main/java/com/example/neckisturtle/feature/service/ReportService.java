package com.example.neckisturtle.feature.service;

import com.example.neckisturtle.core.resultMap;
import com.example.neckisturtle.feature.domain.MissionRecord;
import com.example.neckisturtle.feature.domain.Pose;
import com.example.neckisturtle.feature.domain.User;
import com.example.neckisturtle.feature.persistance.MissionRecordRepo;
import com.example.neckisturtle.feature.persistance.PoseRepo;
import com.example.neckisturtle.feature.persistance.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReportService {

    private final UserRepo userRepo;
    private final PoseRepo poseRepo;

    private final MissionRecordRepo missionRecordRepo;

    public ReportService(UserRepo userRepo, PoseRepo poseRepo, MissionRecordRepo missionRecordRepo) {
        this.userRepo = userRepo;
        this.poseRepo = poseRepo;
        this.missionRecordRepo = missionRecordRepo;
    }

    public resultMap getDayInfo(String email, String day) throws ParseException {

        User user = userRepo.findByEmail(email).orElseThrow();

        Pose defaultPose = new Pose();
        defaultPose.setPoseTime(0,0);

        Pose pose = poseRepo.findByRegDtmAndUserId(new SimpleDateFormat("yyyy-MM-dd").parse(day), user).orElse(defaultPose);

        List<MissionRecord> mission = missionRecordRepo.findAllByCompleteDtmAndUserId(
                new SimpleDateFormat("yyyy-MM-dd").parse(day), user);

        ArrayList<Integer> simpleMission = new ArrayList<Integer>();

        for (MissionRecord record : mission) {
            simpleMission.add(record.getMissionId().getMission_id());
        }

        resultMap result = new resultMap();

        result.put("turtleTime", pose.getTurtleTime());
        result.put("straightTime", pose.getStraightTime());
        result.put("userName", user.getName());
        result.put("todayDate", day);
        result.put("mission", simpleMission);

        return result;
    }
}
