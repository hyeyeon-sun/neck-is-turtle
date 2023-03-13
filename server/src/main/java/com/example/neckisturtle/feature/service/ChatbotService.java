package com.example.neckisturtle.feature.service;

import com.example.neckisturtle.core.resultMap;
import com.example.neckisturtle.feature.domain.MissionRecord;
import com.example.neckisturtle.feature.domain.Pose;
import com.example.neckisturtle.feature.domain.User;
import com.example.neckisturtle.feature.persistance.MissionRecordRepo;
import com.example.neckisturtle.feature.persistance.PoseRepo;
import com.example.neckisturtle.feature.persistance.UserRepo;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.apache.commons.net.util.Base64;


import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.*;
@Service
@Slf4j
public class ChatbotService {


    private final UserRepo userRepo;

    private final PoseRepo poseRepo;

    private final MissionRecordRepo missionRecordRepo;


    public static String alg = "AES/ECB/PKCS5Padding";
    private final String key = "testtestsetstetsetst";

    public static final byte[] KEY = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P'};
    //private final String iv = key.substring(0, 16); // 16byte


    byte[]        keyBytes = new byte[] {
            0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07,
            0x08, 0x09, 0x0a, 0x0b, 0x0c, 0x0d, 0x0e, 0x0f,
    };

    byte[]          ivBytes = new byte[] {
            0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00,
            0x07, 0x06, 0x05, 0x04, 0x03, 0x02, 0x01, 0x00};


    public ChatbotService(UserRepo userRepo, PoseRepo poseRepo,  MissionRecordRepo missionRecordRepo) {
        this.userRepo = userRepo;
        this.poseRepo = poseRepo;
        this.missionRecordRepo = missionRecordRepo;
    }


    public Key generateKey(String key) throws Exception {
        Key keySpec;

        byte[] keyBytes = new byte[16];
        byte[] b = key.getBytes("UTF-8");

        int len = b.length;
        if (len > keyBytes.length) {
            len = keyBytes.length;
        }

        System.arraycopy(b, 0, keyBytes, 0, len);
        keySpec = new SecretKeySpec(keyBytes, "AES");

        return keySpec;
    }


    public String encrypt(String text) throws Exception {

        SecretKeySpec ks = new SecretKeySpec(KEY, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, ks);

        byte[] encryptedBytes = cipher.doFinal(text.getBytes());
        String result = Base64.encodeBase64String(encryptedBytes);
        String keyForJS = Base64.encodeBase64String(KEY);
        System.out.println("THIS KEY WILL BE USED FOR JS-SIDE = " + keyForJS);
        return result;
    }

    public resultMap decrypt(String encryptedValue) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException {

        SecretKeySpec ks = new SecretKeySpec(KEY, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, ks);


        byte[] decryptedValue = Base64.decodeBase64(encryptedValue.getBytes());
        byte[] decValue;
        try {
            decValue = cipher.doFinal(decryptedValue);

            JSONParser parser = new JSONParser();
            String value = new String(decValue);
            System.out.println("value ::"+ value);
            JSONObject jsonObj = (JSONObject) parser.parse(value);

            resultMap result = new resultMap();

            result.put("turtleTime", jsonObj.get("turtleTime"));
            result.put("straightTime", jsonObj.get("straightTime"));
            result.put("userName", jsonObj.get("userName"));
            result.put("todayDate", jsonObj.get("todayDate"));
            result.put("mission", jsonObj.get("mission"));

            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public resultMap getTodayInfo(String kakaoId) {
        try {

            //===============step1. get pose and mission info ==================/

        User kakaoUser = userRepo.findByKakaoId(kakaoId).orElseThrow();

        Date today = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String format = formatter.format(today);

        User user = userRepo.findByEmail(kakaoUser.getEmail()).orElseThrow();
        Pose defaultPose = new Pose();
        defaultPose.setPoseTime(0,0);

        Pose pose = poseRepo.findByRegDtmAndUserId(new SimpleDateFormat("yyyy-MM-dd").parse(format), user).orElse(defaultPose);

        List<MissionRecord> mission = missionRecordRepo.findAllByCompleteDtmAndUserId(
                new SimpleDateFormat("yyyy-MM-dd").parse(format), user);

        ArrayList<Integer> simpleMission = new ArrayList<Integer>();

        for (MissionRecord record : mission) {
            simpleMission.add(record.getMissionId().getMission_id());
        }

        HashMap<String, Object> infoToFront = new HashMap<>();
        infoToFront.put("turtleTime", pose.getTurtleTime());
        infoToFront.put("straightTime", pose.getStraightTime());
        infoToFront.put("userName", user.getName());
        infoToFront.put("todayDate", format);
        infoToFront.put("mission", simpleMission);

        //======================step2. encrypt contents======================

            JSONObject jsonObj = new JSONObject(infoToFront);
            String encryptedInfo = this.encrypt(jsonObj.toJSONString());

        //======================step3. Process response to fit the Kakao Chatbot API ======================

            //initialize
        ArrayList<resultMap> outputs = new ArrayList<resultMap>();
        resultMap template = new resultMap();
        resultMap basicCard = new resultMap();
        resultMap thumbnail = new resultMap();
        resultMap basicCardWrapper = new resultMap();

        //add card info
        basicCard.put("title", "오늘의 거북목 리포트가 도착했습니다.");
        basicCard.put("description", "반갑소 " + user.getName() + "양. 오늘의 거북목 리포트가 도착했소. 리포트를 보고 싶다면 하단의 링크를 클릭하시오.");
        thumbnail.put("imageUrl", "https://neckisturtlebucket.s3.amazonaws.com/logo.png");
        basicCard.put("thumbnail", thumbnail);
        outputs.add(basicCardWrapper);

        ArrayList<resultMap> buttons = new ArrayList<resultMap>();
        resultMap button = new resultMap();

        //add button(link) info
        button.put("action", "webLink");
        button.put("label", "리포트 보러 바로가기");
        button.put("webLinkUrl", "https://www.neckisturtle.com/report/aa?info=" + encryptedInfo);
        //URL aURL = new URL("https", "www.neckisturtle.com", 80 , "/report/"+encryptedInfo);
        //button.put("webLinkUrl", aURL);
        buttons.add(button);
        basicCard.put("buttons", buttons);
        basicCardWrapper.put("basicCard", basicCard);


        template.put("outputs", outputs);
        resultMap result = new resultMap();
        result.put("template", template);
        result.put("version", "2.0");

        return result;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
