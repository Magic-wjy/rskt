package com.rskt.web.controller;


import com.rskt.system.service.IIntelligentSpeechService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("intelligentSpeech")
public class IntelligentSpeechController {

    @Autowired
    private IIntelligentSpeechService iIntelligentSpeechService;

    @PostMapping("/speechDeal")
    public String speechDell(HttpServletResponse response)
    {
        return iIntelligentSpeechService.startRecognize(response);
    }
}
