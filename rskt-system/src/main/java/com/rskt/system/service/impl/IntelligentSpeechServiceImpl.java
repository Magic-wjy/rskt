package com.rskt.system.service.impl;

import com.rskt.system.domain.PythonApi;
import com.rskt.system.service.IIntelligentSpeechService;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.io.File;
import java.io.IOException;

@Service
public class IntelligentSpeechServiceImpl implements IIntelligentSpeechService {

    String filePath = "/coop.wav";  //换上你自己的文件路径

    AudioFormat audioFormat;
    static TargetDataLine targetDataLine;
    boolean flag = true;

    @Override
    public void speechDeal(AudioInputStream inputStream) {
//        AudioInputStream cin = null;
//        try {
//            cin = AudioSystem.getAudioInputStream(new BufferedInputStream(inputStream));
//        } catch (UnsupportedAudioFileException | IOException e) {
//            throw new RuntimeException(e);
//        }
        AudioFormat format = inputStream.getFormat();
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        SourceDataLine line = null;
        try {
            line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(format);//或者line.open();format参数可有可无
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        line.start();
        int nBytesRead = 0;
        byte[] buffer = new byte[512];
        while (true) {
            try {
                nBytesRead = inputStream.read(buffer, 0, buffer.length);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (nBytesRead <= 0){
                break;
            }
            line.write(buffer, 0, nBytesRead);
        }
        line.drain();
        line.close();
        System.out.println("结束播放");
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

    @Override
    public void startRecognize() {
        try {
            // 获得指定的音频格式
            audioFormat = getAudioFormat();
//            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
//            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
//            flag = true;
//            AudioFileFormat.Type fileType = null;
//            fileType = AudioFileFormat.Type.WAVE;
//            //声音录入的权值
//            int weight = 10;
//            ByteArrayInputStream bais = null;
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            AudioInputStream ais = null;
            File audioFile = new File(filePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
            System.out.println(pythonApi(audioInputStream));
            try {
//                targetDataLine.open(audioFormat);
//                targetDataLine.start();
//                byte[] fragment = new byte[1024];
//                long noSoundTime = System.currentTimeMillis();
//                long soundTime = 0L;
//                ais = new AudioInputStream(targetDataLine);
//                while (flag) {
//                    targetDataLine.read(fragment, 0, fragment.length);
//                    //当数组末位大于weight时开始存储字节（有声音传入），一旦开始不再需要判断末位
//                    if (Math.abs(fragment[fragment.length - 1]) > weight || baos.size() > 0) {
//                        baos.write(fragment);
//                        System.out.println("守卫：" + fragment[0] + ",末尾：" + fragment[fragment.length - 1] + ",lenght" + fragment.length);
//                        //判断语音是否停止
//                        if (Math.abs(fragment[fragment.length - 1]) <= weight) {
//                            noSoundTime = System.currentTimeMillis();
//                        } else {
//                            System.out.println("重置有声音的时间");
//                            soundTime = System.currentTimeMillis();
//                        }
////                        计数超过20说明此段时间没有声音传入(值也可更改)
//                        if (noSoundTime - soundTime > 3000L) {
//                            System.out.println("停止录入");
//                            break;
//                        }
//
//                    }
//                }
//                //取得录音输入流
//                audioFormat = getAudioFormat();
//                byte audioData[] = baos.toByteArray();
//                bais = new ByteArrayInputStream(audioData);
//                ais = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());
////                speechDeal(ais);
//                //定义最终保存的文件名
////                System.out.println("开始生成语音文件");
////                AudioSystem.write(ais, fileType, audioFile);
////                pythonApi(ais);
//                stopRecognize();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
//                try {
//                    ais.close();
//                    bais.close();
//                    baos.reset();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PythonApi pythonApi(AudioInputStream inputStream){
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost("http://localhost:8090/voiceProcess/");
        MultipartEntityBuilder build = MultipartEntityBuilder.create();
        build.setContentType(ContentType.MULTIPART_FORM_DATA);
        build.addPart("voice",new FileBody(new File(filePath)));
        build.addTextBody("rate","16000");
        build.addTextBody("width","16");
        HttpEntity entity = build.build();
        post.setEntity(entity);
        PythonApi pythonApi = new PythonApi();
        try {
            HttpResponse execute = client.execute(post);
            HttpEntity responseEntity = execute.getEntity();
            if (null != responseEntity){
                String result = EntityUtils.toString(responseEntity);
                System.out.println(result);
//                pythonApi = JSONObject.parseObject(result, PythonApi.class);

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pythonApi;
    }

}
