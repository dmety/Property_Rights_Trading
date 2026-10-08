package com.example.app.Utils;

import com.example.testforenv.BuildConfig;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.iflytek.cloud.*;
import com.iflytek.cloud.util.ResourceUtil;

/**
 * 科大讯飞语音识别工具类——离线纯本地 .
 */
public class XunfeiSpeechUtil {
    public interface SpeechCallback {
        void onBegin();
        void onEnd();
        void onResult(String text, boolean isLast);
        void onError(String error);
        void onVolume(int volume);
    }

    private final SpeechRecognizer mRecognizer;
    private final SpeechCallback mCallback;
    private boolean isListening = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Context mContext;

    //    APPID
    private static final String MyAPPID = BuildConfig.XUNFEI_APP_ID;

    public XunfeiSpeechUtil(Context context, SpeechCallback callback) {
        mContext = context.getApplicationContext();
        mCallback = callback;

        // 自动防呆初始化讯飞SDK，只执行一次
        if (SpeechUtility.getUtility() == null) {
            // ★★★ 只改这里为你的appid即可 ★★★
            SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "="+MyAPPID);
        }

        mRecognizer = SpeechRecognizer.createRecognizer(mContext, code -> {});
        setOfflineParams();
    }

    private void setOfflineParams() {
        mRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        mRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
        mRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");
        mRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "plain");
        mRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");
        mRecognizer.setParameter(SpeechConstant.VAD_BOS, "8000");
        mRecognizer.setParameter(SpeechConstant.VAD_EOS, "3000");
        String resPath = getResourcePath();
        mRecognizer.setParameter(ResourceUtil.ASR_RES_PATH, resPath);
//        mRecognizer.setParameter(ResourceUtil.ENGINE_START, ResourceUtil.ASR_RES_PATH);
        mRecognizer.setParameter(ResourceUtil.ENGINE_START, "asr");
    }

    private String getResourcePath() {
        // 注意你的assets/iflytek/iat/ 目录下要有common.jet和sms_16k.jet
        StringBuilder sb = new StringBuilder();
        sb.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "iat/common.jet"));
        sb.append(";");
        sb.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "iat/sms_16k.jet"));
        return sb.toString();
    }

    public void startListening() {
        if (!isListening) {
            isListening = true;
            mRecognizer.startListening(mRecognizerListener);
            if (mCallback != null) handler.post(mCallback::onBegin);
        }
    }

    public void stopListening() {
        if (isListening) {
            isListening = false;
            mRecognizer.stopListening();
            if (mCallback != null) handler.post(mCallback::onEnd);
        }
    }

    public void destroy() {
        mRecognizer.cancel();
        mRecognizer.destroy();
    }

    private final RecognizerListener mRecognizerListener = new RecognizerListener() {
        @Override public void onBeginOfSpeech() { }
        @Override public void onEndOfSpeech() { }
        @Override
        public void onResult(RecognizerResult result, boolean isLast) {
            if (mCallback != null && result != null) {
                handler.post(() -> mCallback.onResult(result.getResultString(), isLast));
            }
        }
        @Override
        public void onError(SpeechError error) {
            if (mCallback != null) handler.post(() -> mCallback.onError(error.getPlainDescription(true)));
        }
        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            if (mCallback != null) handler.post(() -> mCallback.onVolume(volume));
        }
        @Override
        public void onEvent(int eventType, int arg1, int arg2, android.os.Bundle obj) { }
    };
}

