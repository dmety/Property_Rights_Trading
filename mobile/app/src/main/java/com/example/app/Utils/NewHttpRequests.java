package com.example.app.Utils;


import android.util.Log;

import com.example.app.Config.EnvConfig;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

import retrofit2.Callback;


public class NewHttpRequests {

    private String modelName = "qwen3-4b"; // 替换为你的模型名称
    private double temperature = 0.1; // 温度参数
    private List<Message> messageHistory; // 存储消息历史

    private static final int CONNECT_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(10); // 10秒连接超时
    private static final int READ_TIMEOUT = (int) TimeUnit.SECONDS.toMillis(60);    // 60秒读取超时

    public NewHttpRequests() {
        messageHistory = new ArrayList<>(); // 初始化消息历史
    }

    public void getAiResponse(String userMessage, AiResponseCallback callback) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                // 添加用户消息到历史
                messageHistory.add(new Message("user", userMessage));

                Log.d("HttpRequests", "getAiResponse method called with message: " + userMessage);
                URL url = new URL(EnvConfig.LOCAL_AI_BACK);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setConnectTimeout(CONNECT_TIMEOUT); // 10秒连接超时
                connection.setReadTimeout(READ_TIMEOUT);    // 30秒读取超时
                connection.setDoOutput(true);

                // 构建请求体
                JSONObject requestBody = new JSONObject();
                requestBody.put("model", modelName);
                requestBody.put("temperature", temperature);
                requestBody.put("stream", false);

                // 构建消息数组
                JSONArray messagesArray = new JSONArray();
                for (Message msg : messageHistory) {
                    JSONObject messageObj = new JSONObject();
                    messageObj.put("role", msg.getRole());
                    messageObj.put("content", msg.getContent());
                    messagesArray.put(messageObj);
                }
                requestBody.put("messages", messagesArray);

                // 发送请求体
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int statusCode = connection.getResponseCode();
                if (statusCode != HttpURLConnection.HTTP_OK) { // 200
                    Log.e("HttpRequests", "Request failed with status code: " + statusCode);
                    return;
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    responseBuilder.append(line);
                }

                in.close();

                JSONObject jsonResponse = new JSONObject(responseBuilder.toString());
                // 解析新的响应结构
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices.length() > 0) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    String aiResponse = message.getString("content");

                    // 添加AI回复到历史
                    messageHistory.add(new Message("assistant", aiResponse));

                    Log.d("HttpRequests", "AI Response: " + aiResponse);
                    callback.onResponse(aiResponse);
                }

            } catch (java.net.SocketTimeoutException e) {
                // 判断是连接超时还是读取超时
                if (e.getMessage().contains("connect timed out")) {
                    Log.e("HttpRequests", "Connect Timeout: " + e.getMessage());
                    callback.onError("连接超时，请检查网络");  // 连接超时
                } else {
                    Log.e("HttpRequests", "Read Timeout: " + e.getMessage());
                    callback.onError("服务器繁忙");     // 读取超时
                }
            } catch (Exception e) {
                Log.e("HttpRequests", "Error in getAiResponse", e);
                callback.onError("其他错误: " + e.getMessage());  // 其他错误
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }).start();
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
        void onResponse(String aiResponse) throws JSONException;
        void onError(String errorMessage);
    }


//  python语音请求

    public void uploadToAsrService(File wavFile, AsrCallback callback) {
        // 自定义 OkHttpClient 设置超时
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)    // 连接超时时间
                .readTimeout(120, TimeUnit.SECONDS)       // 读取超时时间
                .writeTimeout(120, TimeUnit.SECONDS)      // 写入超时时间
                .build();

        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/wav"), wavFile);
        MultipartBody.Part audioPart = MultipartBody.Part.createFormData(
                "audio",
                wavFile.getName(),
                requestFile
        );
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EnvConfig.PYTHON_AI_BACK)
                .client(okHttpClient)  // 设置自定义的 OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<JsonObject> call = apiService.recognizeAudio(audioPart);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONObject json = new JSONObject(response.body().toString());
                        if (json.optInt("code") == 0) {
                            callback.onSuccess(json.optString("text"));
                        } else {
                            callback.onError(json.optString("msg", "识别失败"));
                        }
                    } catch (JSONException e) {
                        callback.onError("响应解析错误");
                    }
                } else {
                    callback.onError("服务器错误: " + response.code());
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    public interface ApiService {
        @Multipart
        @POST("/asr") // 注意保持与后端路由一致
        Call<JsonObject> recognizeAudio(@Part MultipartBody.Part audio);
    }
    public interface AsrCallback {
        void onSuccess(String recognizedText);
        void onError(String errorMessage);
    }
}
