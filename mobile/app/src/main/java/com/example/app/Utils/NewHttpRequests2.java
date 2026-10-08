package com.example.app.Utils;
import android.util.Log;

import com.example.app.Config.EnvConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class NewHttpRequests2 {

    private String modelName = "qwen3-4b";
    private double temperature = 0.1;
    private List<Message> messageHistory;
    private static final int CONNECT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10); // 10秒连接超时
    private static final int READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(30);    // 30秒读取超时
    private static final int MAX_RETRIES = 2; // 最大重试次数

    public NewHttpRequests2() {
        messageHistory = new ArrayList<>();
    }

    public void getAiResponse(String userMessage, AiResponseCallback callback) {
        new Thread(() -> {
            int retryCount = 0;
            boolean success = false;

            while (retryCount <= MAX_RETRIES && !success) {
                HttpURLConnection connection = null;
                try {
                    // 添加用户消息到历史
                    messageHistory.add(new Message("user", userMessage));

                    Log.d("HttpRequests", "Attempt " + (retryCount + 1) + ": Sending request with message: " + userMessage);
                    URL url = new URL(EnvConfig.LOCAL_AI_BACK);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setConnectTimeout(CONNECT_TIMEOUT);
                    connection.setReadTimeout(READ_TIMEOUT);
                    connection.setDoOutput(true);

                    // 构建请求体
                    JSONObject requestBody = buildRequestBody();

                    // 发送请求体
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int statusCode = connection.getResponseCode();
                    if (statusCode != HttpURLConnection.HTTP_OK) {
                        Log.e("HttpRequests", "Request failed with status code: " + statusCode);
                        if (statusCode >= 500 && retryCount < MAX_RETRIES) {
                            retryCount++;
                            Log.d("HttpRequests", "Retrying... Attempt " + (retryCount + 1));
                            continue;
                        }
                        callback.onResponse("服务器错误: " + statusCode);
                        return;
                    }

                    // 读取响应
                    String response = readResponse(connection);
                    JSONObject jsonResponse = new JSONObject(response);

                    // 处理响应
                    processResponse(jsonResponse, callback);
                    success = true;

                } catch (SocketTimeoutException e) {
                    retryCount++;
                    Log.e("HttpRequests", "Timeout on attempt " + retryCount, e);
                    if (retryCount > MAX_RETRIES) {
                        callback.onResponse("请求超时，请稍后再试");
                    } else {
                        Log.d("HttpRequests", "Retrying after timeout...");
                    }
                } catch (Exception e) {
                    Log.e("HttpRequests", "Error in getAiResponse", e);
                    callback.onResponse("请求失败: " + e.getMessage());
                    return;
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

            if (!success && retryCount > MAX_RETRIES) {
                callback.onResponse("请求超时，请稍后再试");
            }
        }).start();
    }

    private JSONObject buildRequestBody() throws JSONException {
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", modelName);
        requestBody.put("temperature", temperature);
        requestBody.put("stream", false);

        JSONArray messagesArray = new JSONArray();
        for (Message msg : messageHistory) {
            JSONObject messageObj = new JSONObject();
            messageObj.put("role", msg.getRole());
            messageObj.put("content", msg.getContent());
            messagesArray.put(messageObj);
        }
        requestBody.put("messages", messagesArray);
        return requestBody;
    }

    private String readResponse(HttpURLConnection connection) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        StringBuilder responseBuilder = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            responseBuilder.append(line);
        }
        in.close();
        return responseBuilder.toString();
    }

    private void processResponse(JSONObject jsonResponse, AiResponseCallback callback) throws JSONException {
        JSONArray choices = jsonResponse.getJSONArray("choices");
        if (choices.length() > 0) {
            JSONObject firstChoice = choices.getJSONObject(0);
            JSONObject message = firstChoice.getJSONObject("message");
            String aiResponse = message.getString("content");

            // 添加AI回复到历史
            messageHistory.add(new Message("assistant", aiResponse));

            Log.d("HttpRequests", "AI Response: " + aiResponse);
            callback.onResponse(aiResponse);
        } else {
            callback.onResponse("未收到有效响应");
        }
    }

    // 内部消息类
    private static class Message {
        private String role;
        private String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        public String getRole() {
            return role;
        }

        public String getContent() {
            return content;
        }
    }

    public interface AiResponseCallback {
        void onResponse(String aiResponse);
    }
}
