package com.rskt.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpeechAI implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean flag;
    private String intent;
    private String text;
}
