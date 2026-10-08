package com.example.app.Utils;

import static android.app.PendingIntent.getActivity;

import android.util.Log;

import com.example.app.Config.EnvConfig;
import com.example.app.components.Message;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HttpRequests {

    private String chatAnyWhereToken = EnvConfig.chatAnyWhereToken;
    private String chatAnyWhereUrl = EnvConfig.chatAnyWhereUrl;
    private List<Message> messageHistory; // 存储消息历史

    public HttpRequests() {
        messageHistory = new ArrayList<>(); // 初始化消息历史
    }
    public void getAiResponse(String userMessage, AiResponseCallback callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                Log.d("HttpRequests", "getAiResponse method called with message: " + userMessage);
                String urlStr = EnvConfig.LOCAL_AI_BACK + URLEncoder.encode(userMessage, "UTF-8");
                URL url = new URL(urlStr);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(5000); // 5秒连接超时
                connection.setReadTimeout(5000);    // 5秒读取超时

                int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) { // 200
                    Log.e("HttpRequests", "Request failed with status code: " + statusCode);
                    return;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }

                in.close();

                JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
                String aiResponse = jsonResponse.getString("response");

                Log.d("HttpRequests", "AI Response: " + aiResponse);
                callback.onResponse(aiResponse);

            } catch (Exception e) {
                Log.e("HttpRequests", "Error in getAiResponse", e);
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
    }



//    public void getAiResponse(String userMessage, AiResponseCallback callback) {
//        // 将用户消息添加到历史记录中
//        messageHistory.add(new Message(userMessage, true));
//
//        new Thread(() -> {
//            try {
//                // 设置请求的 URL
//                URL url = new URL(chatAnyWhereUrl);
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//                // 设置请求方法为 POST
//                connection.setRequestMethod("POST");
//
//                // 设置请求头
//                connection.setRequestProperty("Authorization", "Bearer " + chatAnyWhereToken);
//                connection.setRequestProperty("Content-Type", "application/json");
//                connection.setDoOutput(true);
//
//                // 构建请求的 JSON body
//                JSONObject body = new JSONObject();
//                body.put("model", "gpt-3.5-turbo"); // 模型 ID
//                body.put("stream", true); // 启用流式响应
//
//                // 构建 messages
//                JSONArray messages = new JSONArray();
//                for (Message message : messageHistory) {
//                    JSONObject messageObj = new JSONObject();
//                    messageObj.put("role", message.isUserMessage() ? "user" : "assistant");
//                    messageObj.put("content", message.getContent());
//                    messages.put(messageObj);
//                }
//
//                body.put("messages", messages);
//
//                // 将 body 写入输出流
//                try (OutputStream os = connection.getOutputStream()) {
//                    byte[] input = body.toString().getBytes("utf-8");
//                    os.write(input, 0, input.length);
//                }
//
//                // 处理流式响应
//                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
//                StringBuilder aiResponseBuilder = new StringBuilder();
//                String line;
//
//                while ((line = in.readLine()) != null) {
//                    // 检查每行内容
//                    if (line.isEmpty()) continue;
//
//                    if (line.equals("data: [DONE]")) {
//                        Log.d("AiResponse", "Streaming finished.");
//                        break;
//                    }
//
//                    // 解析 JSON 响应
//                    JSONObject jsonResponse = new JSONObject(line.substring(6)); // 去掉 "data: "
//                    JSONArray choices = jsonResponse.getJSONArray("choices");
//
//                    for (int i = 0; i < choices.length(); i++) {
//                        JSONObject choice = choices.getJSONObject(i);
//                        JSONObject delta = choice.getJSONObject("delta");
//                        if (delta.has("content")) {
//                            aiResponseBuilder.append(delta.getString("content"));
//                        }
//                    }
//                }
//
//                // 将 AI 的响应添加到历史记录中
//                String aiResponse = aiResponseBuilder.toString();
//                messageHistory.add(new Message(aiResponse, false));
//
//                // 回调更新 UI
//                callback.onResponse(aiResponse);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//    }

    public interface AiResponseCallback {
        void onResponse(String aiResponse);
    }


}