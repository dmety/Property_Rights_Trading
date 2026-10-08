package com.example.app.Utils;

import android.util.Log;

import com.example.app.Config.EnvConfig;
import com.example.app.Model.ProjectDetailsResponse;
import com.example.app.Model.Result;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 项目详情网络请求工具类 - 更新版
 */
public class ProjectDataPushAPIUtils {
    private static final String TAG = "ProjectDataPushAPIUtils";

    private interface ApiService {
        @GET("project/getAllProjectInfo")
        Call<ProjectDetailsResponse> getProjectDetails(@Query("projectCode") String projectCode);

        @GET("project/transferRe")
        Call<Result<String>> confirmProjectAudit(
                @Query("projectCode") String projectCode,
                @Query("userId") String userId
        );
    }

    public interface ProjectDetailsCallback {
        void onSuccess(Result<Object> response);
        void onSuccess(String response);
        void onSuccess(ProjectDetailsResponse response);
        void onFailure(String errorMessage);
        void onError(Throwable throwable);
    }

    private static volatile ProjectDataPushAPIUtils instance;
    private final ApiService apiService;

    private ProjectDataPushAPIUtils() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EnvConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static ProjectDataPushAPIUtils getInstance() {
        if (instance == null) {
            synchronized (ProjectDataPushAPIUtils.class) {
                if (instance == null) {
                    instance = new ProjectDataPushAPIUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 确认项目审核
     * @param projectCode 项目编号
     * @param userId 用户ID
     * @param callback 回调接口
     */
    public void confirmProjectAudit(String projectCode, String userId, ProjectDetailsCallback callback) {
        Log.d(TAG, "请求确认转出审核，项目编号: " + projectCode + ", 用户ID: " + userId);
        apiService.confirmProjectAudit(projectCode, userId).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        Log.d(TAG, "转出审核成功: " + result.getMessage());
                        callback.onSuccess(result.getMessage());
                    } else {
                        String errorMsg = result.getMessage() != null ?
                                result.getMessage() : "审核失败";
                        Log.e(TAG, errorMsg);
                        callback.onFailure(errorMsg);
                    }
                } else {
                    String errorMsg = "HTTP错误: " + response.code();
                    try {
                        if (response.errorBody() != null) {
                            errorMsg += " - " + response.errorBody().string();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "错误信息解析失败", e);
                    }
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }
            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Log.e(TAG, "网络请求失败", t);
                callback.onError(t);
            }
        });
    }

    /**
     * 获取项目详情数据
     */
    public void getProjectDetails(String projectCode, ProjectDetailsCallback callback) {
        Log.d(TAG, "请求项目详情，项目编号: " + projectCode);

        Call<ProjectDetailsResponse> call = apiService.getProjectDetails(projectCode);
        call.enqueue(new Callback<ProjectDetailsResponse>() {
            @Override
            public void onResponse(Call<ProjectDetailsResponse> call, Response<ProjectDetailsResponse> response) {
                if (response.isSuccessful()) {
                    ProjectDetailsResponse body = response.body();
                    if (body != null) {
                        if (body.getCode() == 200) {
                            Log.d(TAG, "获取项目详情成功");
                            callback.onSuccess(body);
                        } else {
                            String errorMsg = "服务器错误: " + body.getMessage();
                            Log.e(TAG, errorMsg);
                            callback.onFailure(errorMsg);
                        }
                    } else {
                        Log.e(TAG, "响应体为空");
                        callback.onFailure("服务器返回空数据");
                    }
                } else {
                    String errorMsg = "HTTP错误: " + response.code();
                    try {
                        errorMsg += " - " + response.errorBody().string();
                    } catch (Exception e) {
                        Log.e(TAG, "解析错误信息失败", e);
                    }
                    Log.e(TAG, errorMsg);
                    callback.onFailure(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ProjectDetailsResponse> call, Throwable t) {
                Log.e(TAG, "网络请求失败: " + t.getMessage(), t);
                callback.onError(t);
            }
        });
    }
}
