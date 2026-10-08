package com.example.app.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.app.Config.EnvConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 证书验证API工具类
 * 功能：封装证书验证相关的网络请求操作，提供简化的API调用接口
 */
public class CertificateApiUtils {
    private static final String TAG = "CertificateApiUtils"; // 日志标签

    /**
     * 证书验证服务接口
     * 定义与后端交互的API接口方法
     */
    public interface CertificateService {
        /**
         * 验证证书接口
         * @param projectCode 证书编号请求体
         * @param file 证书图片文件
         * @return 包含验证结果的Call对象
         */
        @Multipart
        @POST("cert/verUpload")
        Call<ApiResponse> verifyCertificate(
                @Part("projectCode") RequestBody projectCode,
                @Part MultipartBody.Part file);
    }

    /**
     * API响应数据模型
     * 映射服务器返回的JSON数据结构
     */
    public static class ApiResponse {
        private boolean success;    // 请求是否成功
        private String message;     // 服务器返回的消息
        private String code;        // 状态码
        private Object BookInfo;    // 证书信息
        private Boolean Sign;       // 签名验证结果

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getCode() {
            return code;
        }

        public Object getBookInfo() {
            return BookInfo;
        }

        public Boolean getSign() {
            return Sign;
        }
    }

    /**
     * 验证结果回调接口
     * 用于处理验证结果的回调
     */
    public interface VerificationCallback {
        /**
         * 验证成功回调
         * @param response 包含验证结果的响应数据
         */
        void onSuccess(ApiResponse response);

        /**
         * 验证失败回调
         * @param errorMessage 错误信息
         */
        void onFailure(String errorMessage);

        /**
         * 发生错误回调
         * @param t 异常信息
         */
        void onError(Throwable t);
    }

    /**
     * 获取Retrofit实例
     * @return 配置好的Retrofit实例
     */
    private static Retrofit getRetrofitInstance() {
        // 配置OkHttp客户端
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时时间
                .readTimeout(30, TimeUnit.SECONDS)     // 读取超时时间
                .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时时间
                .build();

        // 构建Retrofit实例
        return new Retrofit.Builder()
                .baseUrl(EnvConfig.BASE_URL)           // 设置基础URL
                .client(client)                        // 设置OkHttp客户端
                .addConverterFactory(GsonConverterFactory.create()) // 设置Gson转换器
                .build();
    }

    /**
     * 验证证书方法
     * @param context 上下文对象
     * @param fileUri 证书图片URI
     * @param projectCode 证书编号
     * @param callback 验证结果回调
     */
    public static void verifyCertificate(Context context, Uri fileUri, String projectCode, VerificationCallback callback) {
        try {
            // 通过URI获取文件对象
            File file = FileUtils.getFileFromUri(context, fileUri);
            if (file == null || !file.exists()) {
                callback.onFailure("无法访问文件或文件不存在");
                return;
            }

            // 创建文件请求体
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            // 创建Multipart请求体
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            // 创建证书编号请求体
            RequestBody projectCodeBody = RequestBody.create(MediaType.parse("text/plain"), projectCode);

            // 创建服务实例并发送请求
            CertificateService service = getRetrofitInstance().create(CertificateService.class);
            Call<ApiResponse> call = service.verifyCertificate(projectCodeBody, body);

            // 异步执行请求
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                    // 处理成功的响应
                    if (response.isSuccessful() && response.body() != null) {
                        callback.onSuccess(response.body());
                    } else {
                        // 处理响应失败的情况
                        String errorMsg = "服务器响应错误";
                        if (response.errorBody() != null) {
                            try {
                                errorMsg = response.errorBody().string();
                            } catch (IOException e) {
                                Log.e(TAG, "解析错误响应失败", e);
                            }
                        }
                        callback.onFailure(errorMsg);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                    // 处理请求失败的情况
                    callback.onError(t);
                }
            });
        } catch (Exception e) {
            // 处理异常情况
            callback.onError(e);
        }
    }
}
