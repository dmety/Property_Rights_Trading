package com.example.app.Frame;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Utils.NewHttpRequests;
import com.example.testforenv.R;
import com.example.app.Adapter.MarkdownMessageAdapter;
import com.example.app.Utils.XunfeiTtsOfflineUtil;
import com.example.app.components.Message;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;

import java.util.ArrayList;
import java.util.List;

public class AiFragment extends Fragment {
    private static final String TAG = "AiFragment";
    private static final int REQUEST_STORAGE_PERMISSION = 200;

    private NewHttpRequests httpRequests;
    private RecyclerView recyclerView;
    private MarkdownMessageAdapter adapter;
    private List<Message> messageList;
    private EditText inputMessage;
    private Button sendButton;
    private ProgressBar progressBar;
    private Button voiceControlButton;
    private XunfeiTtsOfflineUtil ttsUtil;
    private boolean isTtsInitialized = false;
    private boolean isSpeechPaused = false;
    private String currentSpeechText = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agent, container, false);
        initViews(view);
        checkAndRequestPermissions();
        return view;
    }

    private void initViews(View view) {
        httpRequests = new NewHttpRequests();
        recyclerView = view.findViewById(R.id.recyclerView);
        inputMessage = view.findViewById(R.id.inputMessage);
        sendButton = view.findViewById(R.id.sendButton);
        progressBar = view.findViewById(R.id.progressBar);
        voiceControlButton = view.findViewById(R.id.voiceControlButton);

        // 初始化消息列表
        messageList = new ArrayList<>();
        adapter = new MarkdownMessageAdapter(requireContext(), messageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        // 设置发送按钮监听
        sendButton.setOnClickListener(v -> handleSendMessage());

        // 设置语音控制按钮监听
        voiceControlButton.setOnClickListener(v -> toggleSpeechPlayback());
        voiceControlButton.setVisibility(View.GONE); // 初始隐藏
    }

    /**
     * 切换语音播放状态
     */
    private void toggleSpeechPlayback() {
        if (ttsUtil == null || !isTtsInitialized) {
            showToast("语音功能未就绪");
            return;
        }

        if (ttsUtil.isSpeaking()) {
            ttsUtil.pause();
            isSpeechPaused = true;
            voiceControlButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_play, 0, 0, 0);
        } else if (isSpeechPaused) {
            ttsUtil.resume();
            isSpeechPaused = false;
            voiceControlButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_pause, 0, 0, 0);
        } else if (!currentSpeechText.isEmpty()) {
            // 重新播放当前内容
            speakWithTts(currentSpeechText);
            voiceControlButton.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_pause, 0, 0, 0);
        }
    }

    /**
     * 检查并请求所需权限
     */
    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            initializeTts();
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                showPermissionExplanationDialog();
            } else {
                requestPermissions(
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_STORAGE_PERMISSION);
            }
        }
    }

    /**
     * 显示权限解释对话框
     */
    private void showPermissionExplanationDialog() {
        if (!isAdded()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("需要存储权限")
                .setMessage("离线语音功能需要读取语音资源文件，请授予存储权限")
                .setPositiveButton("确定", (dialog, which) -> {
                    requestPermissions(
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_STORAGE_PERMISSION);
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    showToast("语音功能将不可用");
                    voiceControlButton.setVisibility(View.GONE);
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeTts();
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showPermissionExplanationDialog();
                } else {
                    showPermanentlyDeniedDialog();
                }
                voiceControlButton.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 显示权限被永久拒绝的对话框
     */
    private void showPermanentlyDeniedDialog() {
        if (!isAdded()) return;

        new AlertDialog.Builder(requireContext())
                .setTitle("权限被永久拒绝")
                .setMessage("您已永久拒绝存储权限，请前往应用设置手动开启")
                .setPositiveButton("去设置", (dialog, which) -> {
                    openAppSettings();
                })
                .setNegativeButton("取消", (dialog, which) -> {
                    voiceControlButton.setVisibility(View.GONE);
                })
                .show();
    }

    /**
     * 打开应用设置页面
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", requireContext().getPackageName(), null);
        intent.setData(uri);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "无法打开设置页面", e);
            showToast("无法打开设置页面");
        }
    }

    /**
     * 初始化TTS引擎
     */
    private void initializeTts() {
        ttsUtil = XunfeiTtsOfflineUtil.getInstance(requireContext());
        ttsUtil.init(new XunfeiTtsOfflineUtil.TtsInitCallback() {
            @Override
            public void onInitSuccess() {
                isTtsInitialized = true;
                Log.d(TAG, "TTS初始化成功");
            }

            @Override
            public void onInitFailed(int errorCode) {
                isTtsInitialized = false;
                Log.e(TAG, "TTS初始化失败，错误码: " + errorCode);
                requireActivity().runOnUiThread(() -> {
                    voiceControlButton.setVisibility(View.GONE);
                });
                showToast("语音功能初始化失败");
            }
        });
    }

    private void handleSendMessage() {
        String message = inputMessage.getText().toString().trim();
        if (message.isEmpty()) {
            showToast("请输入消息");
            return;
        }

        // 如果TTS未初始化，不显示语音按钮
        if (!isTtsInitialized) {
            voiceControlButton.setVisibility(View.GONE);
        }

        // 停止当前播放
        if (ttsUtil != null && (ttsUtil.isSpeaking() || isSpeechPaused)) {
            ttsUtil.stop();
            isSpeechPaused = false;
        }

        // 更新UI
        addMessageToChat(message, true);
        inputMessage.setText("");
        setLoadingState(true);

        // 获取AI回复
        httpRequests.getAiResponse(message, new NewHttpRequests.AiResponseCallback() {
            @Override
            public void onResponse(String aiResponse) {
                requireActivity().runOnUiThread(() -> {
                    setLoadingState(false);
                    String displayText = prepareDisplayText(aiResponse);
                    currentSpeechText = prepareSpeechText(aiResponse);

                    addMessageToChat(displayText, false);

                    // 只有TTS初始化成功才显示语音控制按钮
                    if (isTtsInitialized) {
                        voiceControlButton.setVisibility(View.VISIBLE);
                        voiceControlButton.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_pause, 0, 0, 0);
                        speakWithTts(currentSpeechText);
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                requireActivity().runOnUiThread(() -> {
                    setLoadingState(false);
                    addMessageToChat("Error: " + errorMessage, false);
                    voiceControlButton.setVisibility(View.GONE);
                });
            }
        });
    }

    /**
     * 使用TTS播放文本
     */
    private void speakWithTts(String text) {
        if (!isTtsInitialized || ttsUtil == null) {
            return;
        }

        ttsUtil.speak(text, new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {
                Log.d(TAG, "语音播报开始");
                isSpeechPaused = false;
            }

            @Override
            public void onCompleted(SpeechError error) {
                if (error != null) {
                    Log.e(TAG, "语音合成错误: " + error.getErrorDescription());
                    showToast("语音播报失败");
                }
                // 播放完成恢复按钮状态
                voiceControlButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_play, 0, 0, 0);
            }

            @Override
            public void onSpeakPaused() {
                voiceControlButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_play, 0, 0, 0);
            }

            @Override
            public void onSpeakResumed() {
                voiceControlButton.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_pause, 0, 0, 0);
            }

            @Override public void onBufferProgress(int p, int b, int e, String i) {}
            @Override public void onSpeakProgress(int p, int b, int e) {}
            @Override public void onEvent(int t, int a1, int a2, Bundle b) {}
        });
    }

    /**
     * 准备用于Markdown显示的文本
     */
    private String prepareDisplayText(String response) {
        return response.replaceAll("\\n\\n+", "\n").trim();
    }

    /**
     * 准备用于语音合成的文本
     */
    private String prepareSpeechText(String response) {
        String cleaned = response.replaceAll("(?s)<think>.*?</think>", "")
                .replaceAll("^#{1,3}\\s*", "")
                .replaceAll("\\n#{1,3}\\s*", "\n")
                .replaceAll("-{3,}", "")
                .replaceAll("[*+-]\\s", "")
                .replaceAll("[*_]{1,2}", "")
                .replaceAll("^>\\s*", "")
                .replaceAll("\\n\\s*\\n", "\n")
                .replaceAll("\\s{2,}", " ")
                .trim();
        return cleaned;
    }

    private void addMessageToChat(String text, boolean isUser) {
        messageList.add(new Message(text, isUser));
        adapter.notifyItemInserted(messageList.size() - 1);
        recyclerView.smoothScrollToPosition(messageList.size() - 1);
    }

    private void setLoadingState(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        sendButton.setEnabled(!isLoading);
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        if (ttsUtil != null) {
            ttsUtil.release();
        }
        super.onDestroy();
    }
}
