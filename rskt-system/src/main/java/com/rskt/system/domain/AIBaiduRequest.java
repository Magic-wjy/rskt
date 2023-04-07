package com.rskt.system.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class AIBaiduRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String format;

    private String rate;
    private String channel;
    private String cuid;

    private String token;
    private String speech;
    private Integer len;

    private Integer dev_pid;
    private Integer lm_id;


}
