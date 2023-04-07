package com.rskt.web;

import com.alibaba.fastjson2.JSONObject;
import com.rskt.system.domain.AIBaiduRequest;
import okhttp3.*;

import java.io.IOException;
import java.util.Base64;

public class AIBaiduUtil {
    public static final String API_KEY = "N1CC7ocMBHPacdE1xiqgwGiD";
    public static final String SECRET_KEY = "1FE4Bpa48vbY2AFINoG40Vo7KuEtElKj";


    public static final String CUID = "29044817";


    //    String filePath = "/coop.wav";  //换上你自己的文件路径
    static String filePath = "D:\\qq数据\\1015024464\\FileRecv\\1_coop.wav";  //换上你自己的文件路径


    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

//    public static void main(String []args) throws IOException{
//        MediaType mediaType = MediaType.parse("application/json");
//        // speech 可以通过 getFileContentAsBase64("C:\fakepath\1_coop.wav") 方法获取,如果Content-Type是application/x-www-form-urlencoded时,使用getFileContentAsBase64Urlencoded方法获取
//        RequestBody body = RequestBody.create(mediaType, "{\"format\":\"wav\",\"rate\":16000,\"channel\":1,\"cuid\":\"1FE4Bpa48vbY2AFINoG40Vo7KuEtElKj\",\"speech\":\"UklGRiS8AgBXQVZFZm10IBAAAAABAAEAgD4AAAB9AAACABAAZGF0YQC8AgDYAV4A7v+MAcgCIwIXAfUAuwHlAUUBTwEyALz+5P6O...\",\"len\":179244,\"token\":\"" + getAccessToken() + "\",\"token\":\"::token\"}");
//        Request request = new Request.Builder()
//                .url("https://vop.baidu.com/server_api")
//                .method("POST", body)
//                .addHeader("Content-Type", "application/json")
//                .addHeader("Accept", "application/json")
//                .build();
//        Response response = HTTP_CLIENT.newCall(request).execute();
//        System.out.println(response.body().string());
//        System.out.println(voiceToText());
//
//    }

    public static String voiceToText(byte[] b) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        AIBaiduRequest param = new AIBaiduRequest();
        param.setFormat("wav");
        param.setRate("16000");
        param.setChannel("1");
        param.setCuid(CUID);
        param.setToken(getAccessToken());
//        byte[] b = Files.readAllBytes(Paths.get(filePath));
        String speech = Base64.getEncoder().encodeToString(b);
        param.setSpeech(speech);
        param.setLen(b.length);
        String s = JSONObject.toJSONString(param);
        RequestBody body = RequestBody.create(mediaType, s);
        Request request = new Request.Builder()
                .url("https://vop.baidu.com/server_api")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        if (null != response.body() ) {
            JSONObject jsonObject = JSONObject.parseObject(response.body().string());
            if ("success.".equals(jsonObject.getString("err_msg"))){
                return jsonObject.getString("result");
            }
        }
        return null;
    }

    /**
     * 获取文件base64编码
     * @param b 文件路径
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
    static String getFileContentAsBase64(byte[] b) throws IOException {
//        byte[] b = Files.readAllBytes(Paths.get(path));
        return Base64.getEncoder().encodeToString(b);
    }
    /**
     * 获取文件base64 UrlEncode编码
     * @param path 文件路径
     * @return base64编码信息，不带文件头
     * @throws IOException IO异常
     */
//    static String getFileContentAsBase64Urlencoded(String path) throws IOException {
//        return URLEncoder.encode(getFileContentAsBase64(path), "utf-8");
//    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token?client_id=N1CC7ocMBHPacdE1xiqgwGiD&client_secret=1FE4Bpa48vbY2AFINoG40Vo7KuEtElKj&grant_type=client_credentials")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        String accessToken = JSONObject.parseObject(response.body().string()).getString("access_token");
        return accessToken;
    }
}
