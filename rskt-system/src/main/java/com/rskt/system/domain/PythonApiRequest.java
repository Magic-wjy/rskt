package com.rskt.system.domain;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class PythonApiRequest implements Serializable {
    private MultipartFile voice;
    private float rate;
    private int width;


}
