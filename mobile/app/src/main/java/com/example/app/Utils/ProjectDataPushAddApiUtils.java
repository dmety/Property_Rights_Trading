package com.example.app.Utils;

import android.util.Log;

import com.example.app.Config.EnvConfig;
import com.example.app.Model.Device;
import com.example.app.Model.DeviceData;
import com.example.app.Model.Result;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * 网络请求工具类 - 项目数据推送新增API封装
 * 功能：
 * 1. 使用单例模式管理Retrofit实例
 * 2. 封装项目数据推送新增接口
 * 3. 使用Result类统一处理响应结果
 */
public class ProjectDataPushAddApiUtils {
    private static final String TAG = "ProjectDataPushAddApiUtils"; // 日志标签

    /**
     * Retrofit API接口定义
     */
    private interface ApiService {
//        添加采集点
        @POST("device/addPointByPhone")
        Call<Result<String>> addNewCollectionPoint(
                @Query("pointName") String pointName,
                @Query("projectCode") String projectCode);

//        加载采集点
        @POST("device/getDeviceDataByPhone")
        Call<Result<List<DeviceData>>> getCollectionPoints(@Query("projectCode") String projectCode);

//        根据用户名查找可用设备
        @POST("device/queryDev")
        Call<Result<List<Device>>> getAvailableDevices(@Query("name") String name);

//        采集信息
        @POST("device/start")
        Call<Result<String>> startCollection(
                @Query("id") String id,
                @Query("pointName") String point,
                @Query("projectCode") String code
        );
    }

    // 单例实例
    private static volatile ProjectDataPushAddApiUtils instance;
    // Retrofit服务接口
    private final ApiService apiService;

    /**
     * 私有构造函数（单例模式）
     */
    private ProjectDataPushAddApiUtils() {
        // 配置OkHttpClient
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // 初始化Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(EnvConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    /**
     * 获取单例实例（双重校验锁实现线程安全）
     */
    public static ProjectDataPushAddApiUtils getInstance() {
        if (instance == null) {
            synchronized (ProjectDataPushAddApiUtils.class) {
                if (instance == null) {
                    instance = new ProjectDataPushAddApiUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 新增采集点回调接口
     */
    public interface CollectionPointCallback {
        void onSuccess(Result<String> result);
        void onFailure(Result<String> result);
        void onError(Throwable throwable);
    }
    public void addNewCollectionPoint(String pointName, String projectCode, CollectionPointCallback callback) {
        apiService.addNewCollectionPoint(pointName,projectCode).enqueue(new Callback<Result<String>>() {
            @Override
            public void onResponse(Call<Result<String>> call, Response<Result<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<String> result = response.body();
                    if (result.getCode() == 200) {
                        callback.onSuccess(result);
                    } else {
                        callback.onFailure(result);
                    }
                } else {
                    callback.onFailure(new Result<>(
                            response.code(),
                            "请求失败，状态码: " + response.code(),
                            null
                    ));
                }
            }
            @Override
            public void onFailure(Call<Result<String>> call, Throwable t) {
                Log.e(TAG, "添加采集点失败", t);
                callback.onError(t);
            }
        });
    }
    /**
     * 根据项目编号获取采集点
     * */
    public interface CollectionPointsCallback {
        void onSuccess(Result<List<DeviceData>> result);
        void onFailure(Result<List<DeviceData>> result);
        void onError(Throwable throwable);
    }
    public void getCollectionPoints(String projectCode, CollectionPointsCallback callback) {
        apiService.getCollectionPoints(projectCode).enqueue(new Callback<Result<List<DeviceData>>>() {
            @Override
            public void onResponse(Call<Result<List<DeviceData>>> call,
                                   Response<Result<List<DeviceData>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<DeviceData>> result = response.body();
                    if (result.getCode() == 200) {
                        callback.onSuccess(result);
                    } else {
                        callback.onFailure(result);
                    }
                } else {
                    callback.onFailure(new Result<>(
                            response.code(),
                            "请求失败，状态码: " + response.code(),
                            null
                    ));
                }
            }
            @Override
            public void onFailure(Call<Result<List<DeviceData>>> call, Throwable t) {
                Log.e(TAG, "获取采集点失败", t);
                callback.onError(t);
            }
        });
    }

    /**
     * 根据用户名查找采集设备
     * */
    public interface DeviceListCallback {
        void onSuccess(Result<List<Device>> result);
        void onFailure(Result<List<Device>> result);
        void onError(Throwable throwable);
    }
    public void getAvailableDevices(String username, DeviceListCallback callback) {
        apiService.getAvailableDevices(username).enqueue(new Callback<Result<List<Device>>>() {
            @Override
            public void onResponse(Call<Result<List<Device>>> call,
                                   Response<Result<List<Device>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Result<List<Device>> result = response.body();
                    if (result.getCode() == 200) {
                        callback.onSuccess(result);
                    } else {
                        callback.onFailure(result);
                    }
                } else {
                    callback.onFailure(new Result<>(
                            response.code(),
                            "请求失败，状态码: " + response.code(),
                            null
                    ));
                }
            }
            @Override
            public void onFailure(Call<Result<List<Device>>> call, Throwable t) {
                Log.e(TAG, "获取设备列表失败", t);
                callback.onError(t);
            }
        });
    }

//    设备采集

    public interface CollectionCallback {
        void onSuccess(String response);
        void onFailure(String errorMessage);
        void onError(Throwable throwable);
    }
    public void startCollection(String deviceId, String pointId, String deviceCode,
                                CollectionCallback callback) {
        apiService.startCollection(deviceId, pointId, deviceCode)
                .enqueue(new Callback<Result<String>>() {
                    @Override
                    public void onResponse(Call<Result<String>> call,
                                           Response<Result<String>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Result<String> result = response.body();
                            if (result.getCode() == 200) {
                                callback.onSuccess(result.getData());
                            } else {
                                callback.onFailure(result.getMessage());
                            }
                        } else {
                            callback.onFailure("请求失败，状态码: " + response.code());
                        }
                    }
                    @Override
                    public void onFailure(Call<Result<String>> call, Throwable t) {
                        Log.e(TAG, "采集请求失败", t);
                        callback.onError(t);
                    }
                });
    }

}
