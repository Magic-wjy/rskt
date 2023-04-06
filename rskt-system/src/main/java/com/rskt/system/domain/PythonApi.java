package com.rskt.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class PythonApi implements Serializable {

    private static final long serialVersionUID = 1L;


    private String code;

    private String message;

    private SpeechAI data;
}
