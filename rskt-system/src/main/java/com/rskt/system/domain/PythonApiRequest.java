package com.rskt.system.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class PythonApiRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String voice;
    private float rate;
    private int width;


}
