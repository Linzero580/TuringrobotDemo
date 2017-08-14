package com.linzero.turingrobotdemo.utils;

import com.google.gson.Gson;
import com.linzero.turingrobotdemo.bean.ChatMessage;
import com.linzero.turingrobotdemo.bean.Result;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.RuleBasedCollator;
import java.util.Date;

/**
 * Created by Linzero on 2017/08/11.
 */

public class HttpUtils {
    private static final String URL = "http://www.tuling123.com/openapi/api";
    private static final String API_KEY = "d36a0c943e054a50b0612e4062227a41";


    /**
     * 发送一个消息，得到返回的消息
     *
     * @param msg
     * @return
     */
    public static ChatMessage sendMessag(String msg) {
        ChatMessage chatMessage = new ChatMessage();

        String jsonRes = doGet(msg);
        Gson gson = new Gson();
        Result result = null;
        try {
            result = gson.fromJson(jsonRes, Result.class);
            chatMessage.setMsg(result.getText());
        } catch (Exception e) {
            chatMessage.setMsg("服务器繁忙，请稍后再试");
        }
        chatMessage.setDate(new Date());
        chatMessage.setType(ChatMessage.Type.INCOMING);

        return chatMessage;
    }

    public static String doGet(String msg) {
        String result = "";
        String url = setParams(msg);
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        try {
            java.net.URL urlNet = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) urlNet.openConnection();

            conn.setReadTimeout(5 * 1000);
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");

            is = conn.getInputStream();
            int len = -1;
            byte[] buf = new byte[128];
            baos = new ByteArrayOutputStream();
            while ((len = is.read(buf)) != -1) {
                baos.write(buf, 0, len);
            }
            baos.flush();
            result = new String(baos.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }


    //拼接一个完整的url
    private static String setParams(String msg) {
        String url = "";
        try {
            url = URL + "?key=" + API_KEY + "&info=" + URLEncoder.encode(msg, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url;
    }
}
