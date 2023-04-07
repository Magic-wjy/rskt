package com.rskt.system.service;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.AudioInputStream;

public interface IIntelligentSpeechService {

    public void speechDeal(AudioInputStream inputStream);

    String startRecognize(HttpServletResponse response);
}
