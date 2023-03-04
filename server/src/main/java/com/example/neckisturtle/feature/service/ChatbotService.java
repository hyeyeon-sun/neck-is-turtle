package com.example.neckisturtle.feature.service;

import com.example.neckisturtle.core.resultMap;
import com.example.neckisturtle.feature.domain.Pose;
import com.example.neckisturtle.feature.domain.User;
import com.example.neckisturtle.feature.persistance.PoseRepo;
import com.example.neckisturtle.feature.persistance.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
@Slf4j
public class ChatbotService {

    private final UserRepo userRepo;

    private final PoseRepo poseRepo;

    public ChatbotService(UserRepo userRepo, PoseRepo poseRepo) {
        this.userRepo = userRepo;
        this.poseRepo = poseRepo;
    }

    public resultMap getTodayInfo(String kakaoId) {
        try {
        ArrayList<resultMap> outputs = new ArrayList<resultMap>();


        User kakaoUser = userRepo.findByKakaoId(kakaoId).orElseThrow();


        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(today);

        User user = userRepo.findByEmail(kakaoUser.getEmail()).orElseThrow();
        Pose defaultPose = new Pose();
        defaultPose.setPoseTime(0,0);

        Pose pose = poseRepo.findByRegDtmAndUserId(new SimpleDateFormat("yyyy-MM-dd").parse(format), user).orElse(defaultPose);



//        resultMap text = new resultMap();
//        text.put("text", "hello I'm Ryan");
//        resultMap simpleText = new resultMap();
//        simpleText.put("simpleText", text);
//
//        outputs.add(simpleText);
//        template.put("outputs", outputs);

        resultMap template = new resultMap();
        resultMap result = new resultMap();
        result.put("vesion", "2.0");
        template.put("name", kakaoUser.getName());
        template.put("straightTime", pose.getStraightTime());
        template.put("turtieTime", pose.getTurtleTime());
        template.put("token", "asdfasdf");

        result.put("template", template);
        return result;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }


}
