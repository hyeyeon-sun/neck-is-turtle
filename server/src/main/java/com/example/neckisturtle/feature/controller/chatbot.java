package com.example.neckisturtle.feature.controller;

import com.example.neckisturtle.core.resultMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/chatbot")
@CrossOrigin
public class chatbot {
    @PostMapping(value = "/test", headers = {"Accept=application/json"})
    public resultMap getMain(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response){


        try{
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(params);
            System.out.println(jsonInString);
            int x = 0;
        }catch (Exception e){

        }


        ArrayList<resultMap> outputs = new ArrayList<resultMap>();

        resultMap text = new resultMap();
        text.put("text", "hello I'm Ryan");
        resultMap simpleText = new resultMap();
        simpleText.put("simpleText", text);

        outputs.add(simpleText);

        resultMap template = new resultMap();

        template.put("outputs", outputs);

        resultMap result = new resultMap();
        result.put("vesion", "2.0");
        result.put("template", template);

        return result;

    }
}
