package com.example.neckisturtle.feature.service;

import com.example.neckisturtle.feature.domain.Mission;
import com.example.neckisturtle.feature.domain.MissionRecord;
import com.example.neckisturtle.feature.domain.User;
import com.example.neckisturtle.feature.dto.MissionDto;
import com.example.neckisturtle.feature.dto.MissionListDto;
import com.example.neckisturtle.feature.dto.MissionsDto;
import com.example.neckisturtle.feature.persistance.MissionRecordRepo;
import com.example.neckisturtle.feature.persistance.MissionRepo;
import com.example.neckisturtle.feature.persistance.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MissionService {

    @Autowired
    private final UserRepo userRepo;

    @Autowired
    private final MissionRecordRepo missionRecordRepo;

    @Autowired
    private final MissionRepo missionRepo;

    public MissionService(UserRepo userRepo, MissionRecordRepo missionRecordRepo, MissionRepo missionRepo) {
        this.userRepo = userRepo;
        this.missionRecordRepo = missionRecordRepo;
        this.missionRepo = missionRepo;
    }


    public List<MissionDto> getTodayMission(String email){
        //MissionRecord mission =

        User user = userRepo.findByEmail(email).orElseThrow();

        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");   // yyyy-MM-dd HH:mm:ss
        String format = formatter.format(today);
        try {
            List<MissionRecord> mission = missionRecordRepo.findAllByCompleteDtmAndUserId(
                    new SimpleDateFormat("yyyy-MM-dd").parse(format), user);

            List<MissionRecord> amission = missionRecordRepo.findAllByUserId(user);

            List<MissionDto> collect = mission.stream()
                    .map(m-> new MissionDto(m.getMissionId().getMission_id()))
                    .collect(Collectors.toList());

            return collect;

        }catch(Exception e){

        }
        return null;
    }

    public String missionSuccess(Integer missionId, String email){
        try{
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");   // yyyy-MM-dd HH:mm:ss
            String format = formatter.format(today);

            User user = userRepo.findByEmail(email).orElseThrow();
            Mission amission = missionRepo.findById(missionId).orElseThrow();

            //MissionRecord mission = missionRecordRepo.findByMissionIdAndUserId(amission, user).orElseThrow();

            if (!missionRecordRepo.existsByCompleteDtmAndUserIdAndMissionId(new SimpleDateFormat("yyyy-MM-dd").parse(format), user, amission)) {
                MissionRecord mission = MissionRecord.builder()
                        .userId(user)
                        .missionId(amission)
                        .completeYn("Y")
                        .completeDtm(new SimpleDateFormat("yyyy-MM-dd").parse(format))
                        .regDtm(new SimpleDateFormat("yyyy-MM-dd").parse(format))
                        .build();
                missionRecordRepo.save(mission);
//            mission.setComplateYn("Y");
//            mission.setCompleteDtm(new SimpleDateFormat("yyyy-MM-dd").parse(format));
//            missionRecordRepo.save(mission);
                return "성공";
            }

            return "성공";

        }catch (Exception e) {
            log.info("error : {}", e);
            return "실패";
        }
    }

    public List<MissionsDto> get3MonthPose(String email){
        try{
            Date today = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String format = formatter.format(today);

            Calendar cal = Calendar.getInstance();
            Date todayCal = new SimpleDateFormat("yyyy-MM-dd").parse(format);
            cal.setTime(todayCal);

            cal.add(Calendar.DATE, 1);
            String endDay = formatter.format(cal.getTime());
            cal.add(Calendar.DATE, -90);
            String startDay = formatter.format(cal.getTime());



            User user = userRepo.findByEmail(email).orElseThrow();

            List<MissionRecord> missions = missionRecordRepo.findByCompleteDtmBetweenAndUserIdOrderByRegDtm(new SimpleDateFormat("yyyy-MM-dd").parse(startDay), new SimpleDateFormat("yyyy-MM-dd").parse(endDay), user);

//
//            List<MissionDto> collect = missions.stream()
//                    .map(m-> new MissionDto(m.getMissionId().getMission_id()))
//                    .collect(Collectors.toList());

            List<MissionListDto> collect = missions.stream()
                    .map(m-> new MissionListDto(m.getMissionId().getMission_id(), m.getCompleteDtm()))
                    .collect(Collectors.toList());

            Map<Date, List<MissionListDto>> datas = collect.stream().collect(Collectors.groupingBy(MissionListDto::getComplateDtm));


            List<MissionsDto> result = new ArrayList<MissionsDto>();

            for (Map.Entry<Date, List<MissionListDto>> entry : datas.entrySet()) {
                result.add(new MissionsDto(formatter.format(entry.getKey()), entry.getValue().size()));
            }
//
//            List<MissionsDto> result = data.stream()
//                    .map(m-> new MissionsDto(m.getMissionId().getMission_id(), m.getCompleteDtm()))
//                    .collect(Collectors.toList());



            System.out.println("시작");
            System.out.println(collect);


            // 날짜별로 하나씩 가져와서 넣기 ?
            // 3달짜리를 한번만 가져오고 이걸 바꾸기 ! 이게 낫겠다.

//            List<Pose> poses = missionRecordRepo.findBy(
//                    new SimpleDateFormat("yyyy-MM-dd").parse(startDay),
//                    new SimpleDateFormat("yyyy-MM-dd").parse(endDay));

//            List<WeekPoseDto> collect = poses.stream()
//                    .map(m-> new WeekPoseDto(m.getRegDtm(), m.getStraightTime(), m.getTurtleTime()))
//                    .collect(Collectors.toList());



            return result;

        }catch(Exception e){
            log.info("error : {}", e);
        }

        return null;
    }

}
