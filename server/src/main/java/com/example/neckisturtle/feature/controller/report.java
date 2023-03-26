package com.example.neckisturtle.feature.controller;

import com.example.neckisturtle.core.resultMap;
import com.example.neckisturtle.feature.dto.DayDto;
import com.example.neckisturtle.feature.security.UserDto;
import com.example.neckisturtle.feature.service.ReportService;
import org.json.simple.parser.ParseException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/v1/report")
@CrossOrigin
public class report {

    private final ReportService reportService;

    public report(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping(value = "", headers = {"Accept=application/json"})
    public resultMap getDayInfo(@RequestHeader(value="Authorization") String Authorization, @RequestBody DayDto dto) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException, java.text.ParseException {
        UserDto userDto = (UserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return reportService.getDayInfo(userDto.getEmail(), dto.getDay());
    }
}
