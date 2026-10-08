package com.example.app.Activity.WorkSpace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.app.Model.ProjectDetailsResponse;
import com.example.app.Model.Result;
import com.example.testforenv.R;
import com.example.app.Utils.ProjectDataPushAPIUtils;

/**
 * 项目详情页面Activity
 * 功能：展示选定项目的详细信息
 * 包括：项目基本信息、交易信息、进度等
 */
public class ProjectDataPush extends AppCompatActivity {

    // UI组件声明
    private ProgressBar progressBar;          // 加载进度条

    // 项目信息显示文本视图
    private TextView tvProjectCode;          // 项目编号
    private TextView tvRightNo;              // 产权编号
    private TextView tvProjectName;          // 项目名称
    private TextView tvTransKind;            // 交易种类
    private TextView tvOrgName;              // 交易机构
    private TextView tvProjectStatus;        // 项目进度
    private TextView tvUpPrice;              // 交易价格
    private TextView tvProjectStartDate;     // 起始日期
    private TextView tvProjectEndDate;       // 结束日期

    // 按钮
    private Button btnProjectData;           // 项目数据按钮
    private Button btnConfirmAudit;          // 确认审核按钮

    private String projectCode; // 项目编号

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);  // 设置布局文件

        initViews();     // 初始化UI组件
        initButtons();   // 初始化按钮事件
        loadData();      // 加载项目数据
    }

    /**
     * 初始化所有视图组件
     */
    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);

        tvProjectCode = findViewById(R.id.tv_projectCode);
        tvRightNo = findViewById(R.id.tv_rightNo);
        tvProjectName = findViewById(R.id.tv_projectName);
        tvTransKind = findViewById(R.id.tv_transKind);
        tvOrgName = findViewById(R.id.tv_orgName);
        tvProjectStatus = findViewById(R.id.tv_projectStatus);
        tvUpPrice = findViewById(R.id.tv_upPrice);
        tvProjectStartDate = findViewById(R.id.tv_projectStartDate);
        tvProjectEndDate = findViewById(R.id.tv_projectEndDate);

        btnProjectData = findViewById(R.id.btn_project_data);
        btnConfirmAudit = findViewById(R.id.btn_confirm_audit);
    }

    /**
     * 初始化按钮点击事件
     */
    private void initButtons() {
        btnProjectData.setOnClickListener(v -> onProjectDataClick());
        btnConfirmAudit.setOnClickListener(v -> onConfirmAuditClick());
    }

    /**
     * 项目数据按钮点击事件
     */
    private void onProjectDataClick() {
        // 实现项目数据按钮的业务逻辑

        // 获取当前项目的编号（根据你的实际情况获取）
        // 创建跳转意图
        Intent intent = new Intent(this, ProjectDataPushAdd.class);

        // 添加项目编号参数
        intent.putExtra("PROJECT_CODE", projectCode);

        // 启动活动
        startActivity(intent);
    }

    /**
     * 确认审核按钮点击事件(转出审核)
     */
    private void onConfirmAuditClick() {
        String projectCode = getIntent().getStringExtra("PROJECT_CODE");
        String userId = "12345"; // 示例，应从登录状态获取
        if (projectCode == null || projectCode.isEmpty()) {
            Toast.makeText(this, "无效的项目编号", Toast.LENGTH_SHORT).show();
            return;
        }
        // 指纹验证
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("指纹验证")
                .setSubtitle("请使用指纹确认转出审核")
                .setNegativeButtonText("取消")
                .build();
        BiometricPrompt biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        showLoading(true);
                        performTransferAudit(projectCode, userId);
                    }
                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(ProjectDataPush.this, "指纹验证失败", Toast.LENGTH_SHORT).show();
                    }
                });
        biometricPrompt.authenticate(promptInfo);
    }
    /**
     * 执行转出审核网络请求
     */
    private void performTransferAudit(String projectCode, String userId) {
        ProjectDataPushAPIUtils.getInstance().confirmProjectAudit(projectCode, userId,
                new ProjectDataPushAPIUtils.ProjectDetailsCallback() {
                    @Override
                    public void onSuccess(Result<Object> response) {

                    }

                    @Override
                    public void onSuccess(String successMessage) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(ProjectDataPush.this,
                                    successMessage,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }

                    @Override
                    public void onSuccess(ProjectDetailsResponse response) {

                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(ProjectDataPush.this, errorMessage, Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> {
                            showLoading(false);
                            Toast.makeText(ProjectDataPush.this,
                                    "网络错误: " + throwable.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }

    /**
     * 加载项目数据
     */
    private void loadData() {
        // 从Intent获取传递的项目编号
        String projectCode = getIntent().getStringExtra("PROJECT_CODE");

        // 检查项目编号有效性
        if (projectCode == null || projectCode.isEmpty()) {
            showError("无效的项目编号");
            return;
        }

        showLoading(true);  // 显示加载进度

        // 调用API获取项目详情数据
        ProjectDataPushAPIUtils.getInstance().getProjectDetails(projectCode,
                new ProjectDataPushAPIUtils.ProjectDetailsCallback() {
                    @Override
                    public void onSuccess(Result<Object> response) {

                    }

                    @Override
                    public void onSuccess(String response) {

                    }

                    @Override
                    public void onSuccess(ProjectDetailsResponse response) {
                        showLoading(false);
                        if (response == null || response.getData() == null) {
                            showError("数据解析错误");
                            return;
                        }
                        updateUI(response.getData());
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        showLoading(false);
                        showError(errorMessage);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        showLoading(false);
                        showError("网络错误: " + throwable.getMessage());
                    }
                });
    }

    /**
     * 更新UI显示项目详情
     * @param data 从服务器获取的项目详情数据
     */
    private void updateUI(ProjectDetailsResponse.DataBean data) {
        try {
            // 检查基础信息
            if (data.getBaseInfo() == null) {
                showError("项目基础信息缺失");
                return;
            }

            ProjectDetailsResponse.BaseInfo baseInfo = data.getBaseInfo();
            projectCode = baseInfo.getProjectCode();

            // 设置基本信息的UI
            setTextSafely(tvProjectCode, baseInfo.getProjectCode());
            setTextSafely(tvRightNo, baseInfo.getRightNo());
            setTextSafely(tvProjectName, baseInfo.getProjectName());
            setTextSafely(tvTransKind, getTransKindLabel(baseInfo.getTransKind()));
            setTextSafely(tvOrgName, baseInfo.getOrgName());
            setTextSafely(tvProjectStatus, getProjectStatusLabel(baseInfo.getProjectStatus()));
            setTextSafely(tvProjectStartDate, baseInfo.getProjectStartDate());
            setTextSafely(tvProjectEndDate, baseInfo.getProjectEndDate());

            // 设置交易价格（来自upInfo）
            if (data.getUpInfo() != null && data.getUpInfo().getUpPrice() != null) {
                setTextSafely(tvUpPrice, data.getUpInfo().getUpPrice() + "元");
            } else {
                setTextSafely(tvUpPrice, "未设置");
            }

        } catch (Exception e) {
            Log.e("ProjectDataPush", "更新UI时出错", e);
            showError("数据错误: " + e.getMessage());
        }
    }

    private void setTextSafely(TextView textView, String text) {
        if (textView != null) {
            textView.setText(text != null ? text : "暂无信息");
        }
    }

    /**
     * 交易种类代码转文本
     */
    private String getTransKindLabel(String code) {
        if (code == null) return "未知";
        switch (code) {
            case "A": return "土地";
            case "B": return "林地";
            case "C": return "房屋";
            case "D": return "生产设施";
            case "E": return "知识产权";
            default: return code; // 直接显示原始值，适用于"A"等非数字代码
        }
    }

    /**
     * 项目状态代码转文本
     */
    private String getProjectStatusLabel(String status) {
        if (status == null) return "未知";
        return status; // 根据API返回的可能值直接显示
    }

    /**
     * 显示/隐藏加载状态
     */
    private void showLoading(boolean show) {
        runOnUiThread(() -> {
            if (progressBar != null) {
                progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
            View contentLayout = findViewById(R.id.content_layout);
            if (contentLayout != null) {
                contentLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });
    }




    /**
     * 显示错误信息
     */
    private void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        });
    }
}
