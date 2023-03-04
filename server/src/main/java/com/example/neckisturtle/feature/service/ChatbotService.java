package com.example.neckisturtle.feature.service;

import com.example.neckisturtle.core.resultMap;
import com.example.neckisturtle.feature.domain.Pose;
import com.example.neckisturtle.feature.domain.User;
import com.example.neckisturtle.feature.persistance.PoseRepo;
import com.example.neckisturtle.feature.persistance.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;

@Service
@Slf4j
public class ChatbotService {

    private final UserRepo userRepo;

    private final PoseRepo poseRepo;


    public static String alg = "AES/CBC/PKCS5Padding";
    private final String key = "01234567890123456789012345678901";
    private final String iv = key.substring(0, 16); // 16byte


    public ChatbotService(UserRepo userRepo, PoseRepo poseRepo) {
        this.userRepo = userRepo;
        this.poseRepo = poseRepo;
    }

    public String encrypt(String text) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);

        byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    public String decrypt(String cipherText) throws Exception {
        Cipher cipher = Cipher.getInstance(alg);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
        IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes());
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParamSpec);

        byte[] decodedBytes = Base64.getDecoder().decode(cipherText);
        byte[] decrypted = cipher.doFinal(decodedBytes);
        return new String(decrypted, "UTF-8");
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

        HashMap<String, Object> infoToFront = new HashMap<>();
        infoToFront.put("turtleTime", pose.getTurtleTime());
        infoToFront.put("straightTime", pose.getStraightTime());
        infoToFront.put("userName", user.getName());
        infoToFront.put("date", format);

        String encryptedInfo = this.encrypt(infoToFront.toString());


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
        template.put("encryptedInfo", encryptedInfo);

        result.put("template", template);
        return result;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
