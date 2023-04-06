package com.rskt.system.service;

import javax.sound.sampled.AudioInputStream;

public interface IIntelligentSpeechService {

    public void speechDeal(AudioInputStream inputStream);

    void startRecognize();
}
