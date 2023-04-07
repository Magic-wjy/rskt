package com.rskt.system.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.rskt.system.service.IIntelligentSpeechService;
import okhttp3.*;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import javax.sound.sampled.*;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class IntelligentSpeechServiceImpl implements IIntelligentSpeechService {

//    String filePath = "/coop.wav";  //换上你自己的文件路径
    String filePath = "D:\\qq数据\\1015024464\\FileRecv\\1_coop.wav";  //换上你自己的文件路径


    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

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
    public String startRecognize(HttpServletResponse response) {
        long apiStartTime = System.currentTimeMillis();
        String result = null;
        ByteArrayInputStream bais = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        AudioInputStream ais = null;
        AudioFormat audioFormat;
        try {
            // 获得指定的音频格式
            audioFormat = getAudioFormat();
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
            targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            flag = true;
            AudioFileFormat.Type fileType = null;
            fileType = AudioFileFormat.Type.WAVE;
            //声音录入的权值
            int weight = 10;
//            pythonApi(audioInputStream);
            try {
                targetDataLine.open(audioFormat);
                targetDataLine.start();
                byte[] fragment = new byte[1024];
                long noSoundTime = System.currentTimeMillis();
                long soundTime = 0L;
                ais = new AudioInputStream(targetDataLine);
                long minTime = System.currentTimeMillis();
                while (flag) {
                    targetDataLine.read(fragment, 0, fragment.length);
                    //当数组末位大于weight时开始存储字节（有声音传入），一旦开始不再需要判断末位
                    if (Math.abs(fragment[fragment.length - 1]) > weight || baos.size() > 0) {
                        baos.write(fragment);
                        System.out.println("守卫：" + fragment[0] + ",末尾：" + fragment[fragment.length - 1] + ",lenght" + fragment.length);
                        //判断语音是否停止
                        if (Math.abs(fragment[fragment.length - 1]) <= weight) {
                            noSoundTime = System.currentTimeMillis();
                        } else {
                            System.out.println("重置有声音的时间");
                            soundTime = System.currentTimeMillis();
                        }
//                        计数超过20说明此段时间没有声音传入(值也可更改)
                        if (noSoundTime - soundTime > 3000L) {
                            System.out.println("停止录入");
                            break;
                        }

                    }
                }
                long twoMinTime = System.currentTimeMillis();
                targetDataLine.close();
                //取得录音输入流
                audioFormat = getAudioFormat();
                byte audioData[] = baos.toByteArray();
                bais = new ByteArrayInputStream(audioData);
                ais = new AudioInputStream(bais, audioFormat, audioData.length / audioFormat.getFrameSize());
//                speechDeal(ais);
                //定义最终保存的文件名
//                File audioFile = new File(filePath);
//                System.out.println("开始生成语音文件");
//                AudioSystem.write(ais, fileType, audioFile);
                result = pythonApi(ais);
                response.setContentType("audio/wav;charset=UTF-8");
                response.addHeader("result",new String(result.getBytes(), "ISO8859-1"));
                String res = pythonSpeech(result);
                byte[] speechBety = AIBaiduUtil.textTovoice(res);
//                byte[] speechBety = AIBaiduUtil.testApi();
                BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                out.write(speechBety);
                out.flush();
                out.close();
                stopRecognize();
                System.out.println("+++++++++++++++++++++++++++++++耗时："+((System.currentTimeMillis()-twoMinTime)+(minTime-apiStartTime))+"++++++++++++++++++++++++++++++++++++++++++++");
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    assert ais != null;
                    ais.close();
                    assert bais != null;
                    bais.close();
                    baos.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                assert ais != null;
                ais.close();
                assert bais != null;
                bais.close();
                baos.reset();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public String pythonApi(AudioInputStream inputStream){
//        MediaType mediaType = MediaType.parse("application/json");
        try {
//            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
//            String bytesStr = new String(bytes, "ISO-8859-1");
//            String s1 = IOUtils.toString(inputStream, "GBK");
            byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
            String s = AIBaiduUtil.voiceToText(bytes);
            return s;
//            String encode = Base64.encode(bytes);
            // speech 可以通过 getFileContentAsBase64("C:\fakepath\1_coop.wav") 方法获取,如果Content-Type是application/x-www-form-urlencoded时,使用getFileContentAsBase64Urlencoded方法获取
//            PythonApiRequest param = new PythonApiRequest();
//            param.setRate(16000);
//            param.setWidth(16);
//            PythonData value = new PythonData();
//            value.setValue(s1);
//            String s2 = JSON.toJSONString(value);
//            param.setVoice(s2);
//            String s = JSON.toJSONString(param);
//            RequestBody body = RequestBody.create(mediaType,s);
//            Request request = new Request.Builder()
//                    .url("http://127.0.0.1:8090/voiceProcess/")
//                    .method("POST", body)
//                    .addHeader("Content-Type", "application/json")
//                    .addHeader("Accept", "application/json")
//                    .build();
//            Response response = null;
//            response = HTTP_CLIENT.newCall(request).execute();
//            System.out.println(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

//        PythonApi pythonApi = new PythonApi();
//        try {
//
//            if (null != responseEntity){
//                String result = EntityUtils.toString(responseEntity);
//                System.out.println(result);
//                pythonApi = JSONObject.parseObject(result, PythonApi.class);
//
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    public String pythonSpeech(String speechWord) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        Map<String, String> param = new HashMap<>();
        param.put("voice",speechWord);
//        byte[] b = Files.readAllBytes(Paths.get(filePath));
        String json = JSONObject.toJSONString(param);
        RequestBody body = RequestBody.create(mediaType, json);
        Request request = new Request.Builder()
                .url("http://47.96.41.234:8090/voiceProcess/")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String res = response.body().string();
        if (null != res){
            JSONObject jsonObject = JSONObject.parseObject(res);
            if (Integer.parseInt(jsonObject.getString("code"))  == 200){
                JSONObject data = (JSONObject) jsonObject.get("data");
                return data.getString("text");
            }
        }
        return null;
    }

}
