package com.rskt.system.service.impl;


import com.alibaba.fastjson2.JSONObject;
import com.rskt.system.domain.AIBaiduRequest;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AIBaiduUtil {
    public static final String API_KEY = "N1CC7ocMBHPacdE1xiqgwGiD";
    public static final String SECRET_KEY = "1FE4Bpa48vbY2AFINoG40Vo7KuEtElKj";


    @Autowired
    private static HttpServletResponse res;

    public static final String CUID = "29044817";


    //    String filePath = "/coop.wav";  //换上你自己的文件路径
    static String filePath = "D:\\qq数据\\1015024464\\FileRecv\\1_coop.wav";  //换上你自己的文件路径


    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();

    public static void main(String []args) throws IOException{

    }

    public static byte[] testApi() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "tex=%E5%A4%A7%E7%A5%AD%E5%8F%B8%E5%82%B2%E5%A8%87%E7%9A%84%E6%AD%BB%E5%93%A6%E5%AE%89%E9%9D%99%E7%9A%84%E6%AD%BB%E5%93%A6%E5%AE%89%E9%9D%99%E7%9A%84%E6%AD%BB%E5%93%A6%E5%95%8A%E5%80%92%E8%90%A8%E5%80%92%E8%90%A8%E5%80%92%E8%90%A8&tok="+getAccessToken()+"&cuid=29044817&ctp=1&lan=zh&spd=5&pit=5&vol=5&per=0&aue=6");
        Request request = new Request.Builder()
                .url("https://tsn.baidu.com/text2audio")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept", "*/*")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        byte[] bytes = response.body().bytes();
        return bytes;
    }

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

    public static byte[] textTovoice(String speechWord) throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        HashMap<String, Object> param = new HashMap<>();
        Iterator<Map.Entry<String, Object>> iterator = param.entrySet().iterator();
        param.put("tex",speechWord);
        param.put("tok",getAccessToken());
        param.put("cuid",CUID);
        param.put("ctp",1);
        param.put("lan","zh");
        param.put("spd",5);
        param.put("pit",5);
        param.put("vol",5);
        param.put("per",1);
        param.put("aue",6);
        StringBuilder paramStr = new StringBuilder();
        for (String key:param.keySet()) {
            paramStr.append("&");
            paramStr.append(key);
            paramStr.append("=");
            paramStr.append(param.get(key));
        }
        paramStr.deleteCharAt(0);
        RequestBody body = RequestBody.create(mediaType, paramStr.toString());
        Request request = new Request.Builder()
                .url("https://tsn.baidu.com/text2audio")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("Accept-Encoding", "gzip")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        byte[] wav = response.body().bytes();
        System.out.println(wav);
        return wav;
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
