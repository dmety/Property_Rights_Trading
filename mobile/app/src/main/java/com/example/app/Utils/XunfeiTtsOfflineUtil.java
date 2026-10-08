package com.example.app.Utils;

import com.example.testforenv.BuildConfig;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

/**
 * 科大讯飞离线语音合成工具类（单例模式）
 * 封装所有与语音合成相关的逻辑，包括权限检查、资源验证等
 */
public class XunfeiTtsOfflineUtil {
    private static final String TAG = "XunfeiTtsOffline";
    private static final int REQUEST_STORAGE_PERMISSION = 200;

//    APPID
    private static final String MyAPPID = BuildConfig.XUNFEI_APP_ID;

    private static XunfeiTtsOfflineUtil instance;
    private SpeechSynthesizer mTts;
    private Context mContext;
    private String currentVoice = "xiaofeng"; // 默认发音人
    private boolean isInitialized = false;
    private PermissionCallback mPermissionCallback;

    // 权限回调接口
    public interface PermissionCallback {
        void onPermissionGranted();
        void onPermissionDenied(boolean isPermanentlyDenied);
    }

    // TTS状态回调接口
    public interface TtsInitCallback {
        void onInitSuccess();
        void onInitFailed(int errorCode);
    }

    private XunfeiTtsOfflineUtil(Context context) {
        this.mContext = context.getApplicationContext();
    }

    /**
     * 获取单例实例
     */
    public static synchronized XunfeiTtsOfflineUtil getInstance(Context context) {
        if (instance == null) {
            instance = new XunfeiTtsOfflineUtil(context);
        }
        return instance;
    }

    /**
     * 初始化TTS引擎（包含权限检查和资源验证）
     * @param callback 初始化回调
     */
    public void init(TtsInitCallback callback) {
        if (isInitialized) {
            callback.onInitSuccess();
            return;
        }

        // 检查权限
        checkStoragePermission(new PermissionCallback() {
            @Override
            public void onPermissionGranted() {
                // 权限已授予，初始化引擎
                initTtsEngine(callback);
            }

            @Override
            public void onPermissionDenied(boolean isPermanentlyDenied) {
                callback.onInitFailed(ErrorCode.MSP_ERROR_ACCESS);
            }
        });
    }

    /**
     * 检查存储权限
     * @param callback 权限检查回调
     */
    private void checkStoragePermission(PermissionCallback callback) {
        mPermissionCallback = callback;

        if (ContextCompat.checkSelfPermission(mContext,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            callback.onPermissionGranted();
        } else {
            // 请求权限
            ActivityCompat.requestPermissions((Activity) mContext,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }

    /**
     * 处理权限请求结果（需要在Activity/Fragment的onRequestPermissionsResult中调用）
     */
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION && mPermissionCallback != null) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mPermissionCallback.onPermissionGranted();
            } else {
                boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
                mPermissionCallback.onPermissionDenied(!shouldShowRationale);
            }
            mPermissionCallback = null;
        }
    }

    /**
     * 打开应用设置页面引导用户手动授予权限
     */
    public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to open app settings: " + e.getMessage());
        }
    }

    /**
     * 初始化TTS引擎
     */
    private void initTtsEngine(TtsInitCallback callback) {
        // 1. 初始化SDK（必须！APPID替换为实际值）
        SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "="+MyAPPID);

        // 2. 创建合成对象
        mTts = SpeechSynthesizer.createSynthesizer(mContext, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code == ErrorCode.SUCCESS) {
                    Log.d(TAG, "引擎初始化成功");
                    configureOfflineParams();
                    isInitialized = true;
                    callback.onInitSuccess();
                } else {
                    Log.e(TAG, "初始化失败,错误码：" + code);
                    callback.onInitFailed(code);
                }
            }
        });
    }

    /**
     * 配置离线合成参数
     */
    private void configureOfflineParams() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 设置使用本地引擎
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath());
        // 设置发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, currentVoice);
        // 设置语速
        mTts.setParameter(SpeechConstant.SPEED, "60");
        // 设置音调
        mTts.setParameter(SpeechConstant.PITCH, "90");
        // 设置音量
        mTts.setParameter(SpeechConstant.VOLUME, "70");
        // 设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 禁用音频保存（提升性能）
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, null);
    }

    // 获取发音人资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        // 合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        // 发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/"+currentVoice+".jet"));
        Log.d(TAG, "语音合成器资源路径:"+tempBuffer);
        return tempBuffer.toString();
    }

    /**
     * 语音合成
     * @param text 要合成的文本
     * @param listener 合成回调监听器（可以为空）
     */
    public void speak(String text, SynthesizerListener listener) {
        if (!isInitialized || mTts == null) {
            Log.e(TAG, "语音合成器未初始化或已释放");
            if (listener != null) {
                listener.onCompleted(new SpeechError(ErrorCode.MSP_ERROR_NOT_INIT));
            }
            return;
        }

        int code = mTts.startSpeaking(text, listener != null ? listener : mDefaultListener);
        if (code != ErrorCode.SUCCESS && listener != null) {
            listener.onCompleted(new SpeechError(code));
        }
    }

    // 默认的SynthesizerListener实现
    private final SynthesizerListener mDefaultListener = new SynthesizerListener() {
        @Override
        public void onSpeakBegin() {
            Log.d(TAG, "开始语音合成");
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error != null) {
                Log.e(TAG, "语音合成错误: " + error.getErrorCode() + " - " + error.getErrorDescription());
            } else {
                Log.d(TAG, "语音合成完成");
            }
        }

        @Override public void onBufferProgress(int p, int b, int e, String i) {}
        @Override public void onSpeakPaused() {}
        @Override public void onSpeakResumed() {}
        @Override public void onSpeakProgress(int p, int b, int e) {}
        @Override public void onEvent(int t, int a1, int a2, Bundle b) {}
    };

    /**
     * 停止合成
     */
    public void stop() {
        if (mTts != null) {
            mTts.stopSpeaking();
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mTts != null && mTts.isSpeaking()) {
            mTts.pauseSpeaking();
        }
    }
    /**
     * 恢复播放
     */
    public void resume() {
        if (mTts != null) {
            mTts.resumeSpeaking();
        }
    }
    /**
     * 判断是否正在播放
     */
    public boolean isSpeaking() {
        return mTts != null && mTts.isSpeaking();
    }
    /**
     * 判断是否暂停中
     */
    public boolean isPaused() {
        return mTts != null && !mTts.isSpeaking();
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mTts != null) {
            mTts.stopSpeaking();
            mTts.destroy();
            mTts = null;
            instance = null;
            isInitialized = false;
        }
    }
}

