package com.rskt.web;


import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@RestController
public class ArtificialIntelligence {

    String filePath = "/Users/wangjiayuan/workspace/luyin/coop.wav";  //换上你自己的文件路径

    AudioFormat audioFormat;
static TargetDataLine targetDataLine;
    boolean flag = true;

    public static void main(String args[]) {
//        ArtificialIntelligence engineeCore = new ArtificialIntelligence();
//
//        engineeCore.startRecognize();
        ArtificialIntelligence ai = new ArtificialIntelligence();
        File file = new File("/Users/wangjiayuan/workspace/luyin/coop.wav");
        MultipartFile multipartFile = getMultipartFile(file);
        try {
            ai.boFang(multipartFile);
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private void boFang(MultipartFile file) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        AudioInputStream cin = AudioSystem.getAudioInputStream(new BufferedInputStream(file.getInputStream()));
        AudioFormat format = cin.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);//或者line.open();format参数可有可无
        line.start();
        int nBytesRead = 0;
        byte[] buffer = new byte[512];
        while (true) {
            nBytesRead = cin.read(buffer, 0, buffer.length);
            if (nBytesRead <= 0){
                break;
            }
            line.write(buffer, 0, nBytesRead);
        }
        line.drain();
        line.close();
    }

    public static MultipartFile getMultipartFile(File file) {
        FileItem item = new DiskFileItemFactory().createItem("file"
                , MediaType.MULTIPART_FORM_DATA_VALUE
                , true
                , file.getName());
        try (InputStream input = new FileInputStream(file);
             OutputStream os = item.getOutputStream()) {
            // 流转移
            IOUtils.copy(input, os);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid file: " + e, e);
        }

        return new CommonsMultipartFile(item);
    }


    private void stopRecognize() {
        flag = false;
        targetDataLine.stop();
        targetDataLine.close();
    }

    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        // 8000,11025,16000,22050,44100
        int sampleSizeInBits = 16;
        // 8,16
        int channels = 1;
        // 1,2
        boolean signed = true;
        // true,false
        boolean bigEndian = false;
        // true,false
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }// end getAudioFormat


    private void startRecognize() {
        try {
            // 获得指定的音频格式
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            flag = true;
            new CaptureThread().start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CaptureThread extends Thread {
        public void run() {
            AudioFileFormat.Type fileType = null;
            File audioFile = new File(filePath);
            fileType = AudioFileFormat.Type.WAVE;
            //声音录入的权值
            int weight = 2;
            //判断是否停止的计数
            int downSum = 0;
            ByteArrayInputStream bais = null;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            AudioInputStream ais = null;
            try {
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                byte[] fragment = new byte[1024];

                ais = new AudioInputStream(targetDataLine);
                while (flag) {
                    targetDataLine.read(fragment, 0, fragment.length);
                    //当数组末位大于weight时开始存储字节（有声音传入），一旦开始不再需要判断末位
                    if (Math.abs(fragment[fragment.length - 1]) > weight || baos.size() > 0) {
                        baos.write(fragment);
                        System.out.println("守卫：" + fragment[0] + ",末尾：" + fragment[fragment.length - 1] + ",lenght" + fragment.length);
                        //判断语音是否停止
                        if (Math.abs(fragment[fragment.length - 1]) <= weight) {
                            downSum++;
                        } else {
                            System.out.println("重置奇数");
                            downSum = 0;
                        }
                        //计数超过20说明此段时间没有声音传入(值也可更改)
                        if (downSum > 1000) {
                            System.out.println("停止录入");
                            break;
                        }

                    }
                }

                //取得录音输入流
                audioFormat = getAudioFormat();
                byte audioData[] = baos.toByteArray();
                bais = new ByteArrayInputStream(audioData);
                ais = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());
                //定义最终保存的文件名
                System.out.println("开始生成语音文件");
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE, audioFile);
                downSum = 0;
                stopRecognize();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //关闭流

                try {
                    ais.close();
                    bais.close();
                    baos.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
