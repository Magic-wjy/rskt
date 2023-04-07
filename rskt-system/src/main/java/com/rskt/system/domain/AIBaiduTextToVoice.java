package com.rskt.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class AIBaiduTextToVoice implements Serializable {

    private static final long serialVersionUID = 1L;


    private String tex;
    private String tok;
    private String cuid;
    private Integer ctp;
    private String format;
    private String lan;
    private Integer spd;
    private Integer pit;
    private Integer vol;
    private Integer per;
    private Integer aue;

}
