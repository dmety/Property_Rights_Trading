package com.example.app.Utils;

import android.content.Context;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.app.Frame.WorkSpace.DoneFragment;

import java.util.concurrent.Executor;

public class FingerVerification {

    public static void authenticateWithBiometrics(Context context, FragmentActivity activity,
                                                  OnBiometricAuthSuccess onSuccess,
                                                  OnBiometricAuthFailure onFailure) {
        BiometricManager biometricManager = BiometricManager.from(context);

        // 检查设备是否支持生物识别功能
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // 生物识别功能可用
                Executor executor = ContextCompat.getMainExecutor(context);
                BiometricPrompt biometricPrompt = new BiometricPrompt(activity, executor,
                        new BiometricPrompt.AuthenticationCallback() {
                            @Override
                            public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                                super.onAuthenticationSucceeded(result);
                                // 验证成功，执行成功的回调
                                onSuccess.onSuccess();
                            }

                            @Override
                            public void onAuthenticationError(int errorCode, CharSequence errString) {
                                super.onAuthenticationError(errorCode, errString);
                                // 验证错误时，执行失败的回调
                                onFailure.onFailure();
                            }

                            @Override
                            public void onAuthenticationFailed() {
                                super.onAuthenticationFailed();
                                // 验证失败
                                onFailure.onFailure();
                            }
                        });

                // 配置指纹验证提示框
                BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("指纹验证")
                        .setSubtitle("请使用指纹解锁")
                        .setDescription("将手指放在指纹传感器上")
                        .setNegativeButtonText("取消")
                        .build();

                // 显示指纹验证框
                biometricPrompt.authenticate(promptInfo);
                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                // 设备没有生物识别硬件
                onFailure.onFailure();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                // 生物识别功能不可用
                onFailure.onFailure();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // 没有注册指纹
                onFailure.onFailure();
                break;

            default:
                onFailure.onFailure();
                break;
        }
    }

    public static void authenticateWithBiometrics(DoneFragment doneFragment, DoneFragment doneFragment1, OnBiometricAuthSuccess 指纹验证成功, OnBiometricAuthFailure 指纹验证失败) {
    }

    // 回调接口，定义指纹验证成功和失败的操作
    public interface OnBiometricAuthSuccess {
        void onSuccess();
    }

    public interface OnBiometricAuthFailure {
        void onFailure();
    }
}
