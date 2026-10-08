package com.example.app.Utils;

import android.util.Log;

import com.example.app.Config.EnvConfig;
import com.example.app.Model.AssigneeMainDetailsResponse;
import com.example.app.Model.AssigneeMainResponse;
import com.example.app.Model.TransfereeModel;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 网络请求工具类 - 项目相关API封装
 * 功能：
 * 1. 使用单例模式管理Retrofit实例
 * 2. 封装项目列表、权证详情和受让方提交接口
 * 3. 统一处理网络请求和响应
 */
public class AssigneeMainApiUtils {
    private static final String TAG = "AssigneeMainApiUtils"; // 日志标签

    /**
     * Retrofit API接口定义
     * 包含所有项目相关的网络请求方法
     */
    private interface ApiService {
        // 获取项目列表
        @GET("project/getTradeStep")
        Call<AssigneeMainResponse> getAllProjects();

        // 根据权证ID获取详情
        @GET("right/getRightById")
        Call<AssigneeMainDetailsResponse> getRightDetails(@Query("rightNo") String rightNo);

        // 提交受让方信息
        @POST("project/addTransferee")
        Call<BasicResponse> submitTransferee(@Body TransfereeModel transferee);
    }

    /**
     * 基础响应模型
     * 用于解析服务器返回的通用响应结构
     */
    public static class BasicResponse {
        private boolean statusOK;  // 请求状态标识
        private String message;    // 服务器返回消息

        public boolean isStatusOK() {
            return statusOK;
        }

        public String getMessage() {
            return message;
        }
    }

    /**
     * 项目列表回调接口
     */
    public interface ProjectListCallback {
        void onSuccess(AssigneeMainResponse response);  // 请求成功且数据有效
        void onFailure(String errorMessage);           // 服务器返回错误
        void onError(Throwable throwable);             // 网络请求失败
    }

    /**
     * 受让方提交回调接口
     */
    public interface AssigneeCallback {
        void onSuccess(String message);       // 提交成功
        void onFailure(String errorMessage);  // 提交失败
        void onError(Throwable throwable);    // 网络错误
    }

    /**
     * 权证详情回调接口
     */
    public interface DetailsCallback {
        void onSuccess(AssigneeMainDetailsResponse response);
        void onFailure(String errorMessage);
        void onError(Throwable throwable);
    }

    // 单例实例
    private static volatile AssigneeMainApiUtils instance;
    // Retrofit服务接口
    private final ApiService apiService;

    /**
     * 私有构造函数（单例模式）
     * 初始化Retrofit和OkHttpClient
     */
    private AssigneeMainApiUtils() {
        // 配置OkHttpClient（设置超时时间）
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)   // 连接超时
                .readTimeout(30, TimeUnit.SECONDS)      // 读取超时
                .writeTimeout(30, TimeUnit.SECONDS)     // 写入超时
                .build();

        // 初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EnvConfig.BASE_URL)       // 基础URL
                .client(client)                    // 设置OkHttpClient
                .addConverterFactory(GsonConverterFactory.create())  // Gson转换器
                .build();

        // 创建API服务实例
        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 获取单例实例（双重校验锁实现线程安全）
     */
    public static AssigneeMainApiUtils getInstance() {
        if (instance == null) {
            synchronized (AssigneeMainApiUtils.class) {
                if (instance == null) {
                    instance = new AssigneeMainApiUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 加载项目列表数据
     * @param callback 结果回调接口
     */
    public void loadProjects(ProjectListCallback callback) {
        apiService.getAllProjects().enqueue(new Callback<AssigneeMainResponse>() {
            @Override
            public void onResponse(Call<AssigneeMainResponse> call,
                                   Response<AssigneeMainResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 请求成功且数据有效
                    callback.onSuccess(response.body());
                } else {
                    // 服务器返回错误状态码
                    String errorMsg = "请求失败，状态码: " + response.code();
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AssigneeMainResponse> call, Throwable t) {
                // 网络请求失败
                Log.e(TAG, "获取项目列表失败", t);
                callback.onError(t);
            }
        });
    }

    /**
     * 获取权证详情
     * @param rightNo 权证编号
     * @param callback 结果回调接口
     */
    public void getRightDetails(String rightNo, DetailsCallback callback) {
        apiService.getRightDetails(rightNo).enqueue(new Callback<AssigneeMainDetailsResponse>() {
            @Override
            public void onResponse(Call<AssigneeMainDetailsResponse> call,
                                   Response<AssigneeMainDetailsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("获取详情失败，状态码: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AssigneeMainDetailsResponse> call, Throwable t) {
                callback.onError(t);
            }
        });
    }

    /**
     * 提交受让方信息
     * @param transferee 受让方数据模型
     * @param callback 结果回调接口
     */
    public void submitTransferee(TransfereeModel transferee, AssigneeCallback callback) {
        apiService.submitTransferee(transferee).enqueue(new Callback<BasicResponse>() {
            @Override
            public void onResponse(Call<BasicResponse> call, Response<BasicResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "服务器响应: " + response.body().getMessage());

                    // 根据服务器返回的message判断是否成功
                    if (response.body().getMessage().equals("Success")) {
                        callback.onSuccess("受让方信息提交成功");
                    } else {
                        // 服务器返回业务错误
                        callback.onFailure(response.body().getMessage());
                    }
                } else {
                    // HTTP状态码错误
                    callback.onFailure("提交失败，状态码: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<BasicResponse> call, Throwable t) {
                // 网络请求失败
                callback.onError(t);
            }
        });
    }
}
