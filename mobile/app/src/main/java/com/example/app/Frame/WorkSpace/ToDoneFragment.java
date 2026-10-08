package com.example.app.Frame.WorkSpace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.app.Activity.WorkSpace.ProjectDataPush;
import com.example.app.Config.EnvConfig;
import com.example.app.Model.ProjectBaseInfo;
import com.example.app.Utils.OkHttpUtil;
import com.example.testforenv.R;
import com.example.app.Activity.WorkSpace.DoneActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ToDoneFragment extends Fragment {

    private LinearLayout cardContainer;
    private String userId = "12345";  // 示例用户ID，实际可以从登录状态中获取

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todone, container, false);
        cardContainer = rootView.findViewById(R.id.cardContainer);

        loadProjectData();

        return rootView;
    }

    // 重新加载数据
    private void loadProjectData() {
        OkHttpUtil.sendGetRequest(EnvConfig.REVIEW_TODO_LIST_URL, new OkHttpUtil.OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) throws JSONException {
                JSONObject responseObject = new JSONObject(response);
                JSONArray dataArray = responseObject.getJSONArray("data");

                getActivity().runOnUiThread(() -> {
                    cardContainer.removeAllViews();
                    for (int i = 0; i < dataArray.length(); i++) {
                        try {
                            JSONObject dataObject = dataArray.getJSONObject(i);
                            Log.d("DoneFragment", dataObject.toString());
                            String projectNumber = dataObject.getString("projectCode");
                            String projectName = dataObject.getString("projectName");
                            String projectStatus = dataObject.getString("projectStatus");
                            String projectAuditor = dataObject.getString("transKind");
                            addCardItem(projectNumber, projectName, projectStatus, projectAuditor);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                return null;
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onError(String error) {
                // 错误处理
            }
        });
    }

    private void addCardItem(String projectNumber, String projectName, String projectStatus, String projectAuditor) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cardView = inflater.inflate(R.layout.card_todone, cardContainer, false);
        TextView projectNumberText = cardView.findViewById(R.id.project_number);
        TextView projectNameText = cardView.findViewById(R.id.project_name);
        TextView projectStatusText = cardView.findViewById(R.id.project_status);
        TextView projectAuditorText = cardView.findViewById(R.id.project_auditor);
        ImageView iconUp = cardView.findViewById(R.id.icon_up);
        ImageView iconDown = cardView.findViewById(R.id.icon_down);

        projectNumberText.setText(projectNumber);
        projectNameText.setText(projectName);
        projectStatusText.setText(projectStatus);
        projectAuditorText.setText(projectAuditor);

        iconUp.setOnClickListener(v -> {
            Log.d("DoneFragment", "点击了查看详细");
            Intent intent = new Intent(getContext(), ProjectDataPush.class);
            // 获取文本内容而不是视图对象
            intent.putExtra("PROJECT_CODE", projectNumberText.getText().toString());
            startActivity(intent);
        });

        iconDown.setOnClickListener(v -> {
            // 传递项目编号和 userId 给指纹验证
            showBiometricPrompt(projectNumber, userId);
        });

        cardContainer.addView(cardView);
    }

    private void showBiometricPrompt(String projectNumber, String userId) {
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("指纹验证")
                .setSubtitle("请使用指纹解锁")
                .setNegativeButtonText("取消")
                .build();

        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(requireContext()),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        // 验证成功后处理项目编号和 userId
                        Log.d("Biometric", "指纹验证成功");
                        Log.d("Biometric", "项目编号: " + projectNumber + ", 用户ID: " + userId);
                        // 发送网络请求并在成功后重新加载页面
                        OkHttpUtil.sendGetRequest(EnvConfig.TransFerReURL + "?projectCode=" + projectNumber + "&userId=" + userId, new OkHttpUtil.OkHttpCallback() {
                            @Override
                            public List<ProjectBaseInfo> onSuccess(String response) throws Exception {
                                Log.d("转出审核成功", "验证成功");
                                // 请求成功后刷新整个 Fragment
                                refreshFragment();
                                return null;
                            }

                            @Override
                            public void onFailure(Exception e) {
                                // 错误处理
                            }

                            @Override
                            public void onError(String error) {
                                // 错误处理
                            }
                        });
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Log.d("Biometric", "指纹验证失败");
                    }
                });
        biometricPrompt.authenticate(promptInfo);
    }

    // 刷新当前 Fragment
    private void refreshFragment() {
        if (getFragmentManager() != null) {
            getFragmentManager().beginTransaction()
                    .detach(this)
                    .attach(this)
                    .commit();
        }
    }

}
