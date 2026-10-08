package com.example.app.Activity.Me.Info;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.app.Model.AssigneeMainDetailsResponse;
import com.example.testforenv.R;
import com.example.app.Utils.AssigneeMainApiUtils;

/**
 * 项目/权证详情页面Activity
 * 功能：展示选定项目的详细权证信息
 * 包括：权证基本信息、权属信息、流转期限等
 */
public class AssigneeMainDetails extends AppCompatActivity {

    // UI组件声明
    private ProgressBar progressBar;          // 加载进度条

    // 权证信息显示文本视图
    private TextView projectNameView;         // 项目名称
    private TextView projectCodeView;         // 项目编号
    private TextView rightOrganView;          // 受理机构
    private TextView aptUserView;             // 受理登记人
    private TextView rightOwnerView;          // 权利人
    private TextView rightNameView;           // 权证名称
    private TextView orgNameView;             // 确权机构
    private TextView ownerShipView;           // 权属性质
    private TextView transferPeriodView;      // 流转期限

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignee_details);  // 设置布局文件

        initViews();     // 初始化UI组件
        loadDetails();   // 加载详情数据
    }

    /**
     * 初始化所有视图组件
     */
    private void initViews() {
        progressBar = findViewById(R.id.progress_bar);

        // 绑定布局中的TextView组件
        projectNameView = findViewById(R.id.project_name);
        projectCodeView = findViewById(R.id.project_code);
        rightOrganView = findViewById(R.id.right_organ);
        aptUserView = findViewById(R.id.apt_user);
        rightOwnerView = findViewById(R.id.right_owner);
        rightNameView = findViewById(R.id.right_name);
        orgNameView = findViewById(R.id.org_name);
        ownerShipView = findViewById(R.id.owner_ship);
        transferPeriodView = findViewById(R.id.transfer_period);
    }

    /**
     * 加载项目详情数据
     */
    private void loadDetails() {
        // 从Intent获取传递的权利编号
        String rightNo = getIntent().getStringExtra("RIGHT_NO");

        // 检查权利编号有效性
        if (rightNo == null) {
            Toast.makeText(this, "无效的项目编号", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        showLoading(true);  // 显示加载进度

        // 调用API获取详情数据
        AssigneeMainApiUtils.getInstance().getRightDetails(rightNo,
                new AssigneeMainApiUtils.DetailsCallback() {
                    @Override
                    public void onSuccess(AssigneeMainDetailsResponse response) {
                        showLoading(false);
                        if (response.getCode() == 200) {
                            // 成功获取数据，更新UI
                            updateUI(response.getData());
                        } else {
                            // 服务器返回错误信息
                            showError(response.getMessage());
                        }
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
     * @param data 从服务器获取的详情数据
     */
    private void updateUI(AssigneeMainDetailsResponse.DataBean data) {
        // 获取权证基础信息
        AssigneeMainDetailsResponse.RightCommonInfo commonInfo = data.getRightCommonInfo();

        // 设置各字段显示内容（带固定前缀）
        projectNameView.setText(String.format("项目名称：%s", commonInfo.getRightName()));
        projectCodeView.setText(String.format("产权编号：%s", commonInfo.getRightNo()));
        rightOrganView.setText(String.format("受理机构：%s", commonInfo.getOrgName()));
        aptUserView.setText(String.format("受理登记人：%s", commonInfo.getUserName()));
        rightOwnerView.setText(String.format("权利人：%s", commonInfo.getRightOwner()));
        rightNameView.setText(String.format("权证名称：%s", commonInfo.getRightName()));
        orgNameView.setText(String.format("确权机构：%s", commonInfo.getOrgName()));
        ownerShipView.setText(String.format("权属性质：%s", getOwnershipLabel(commonInfo.getOwnerShip())));

        // 设置流转期限（开始日期-结束日期）
        transferPeriodView.setText(String.format("拟转出期限：%s —— %s",
                commonInfo.getUseStartDate(), commonInfo.getUseEndDate()));
    }

    /**
     * 将权属代码转换为可读文本
     * @param code 权属代码
     * @return 权属类别文字描述
     */
    private String getOwnershipLabel(String code) {
        switch (code) {
            case "0": return "国有";
            case "1": return "集体所有";
            case "2": return "个人所有";
            default: return "未知";
        }
    }

    /**
     * 显示/隐藏加载状态
     * @param show 是否显示加载状态
     */
    private void showLoading(boolean show) {
        runOnUiThread(() -> {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            findViewById(R.id.content_layout).setVisibility(show ? View.GONE : View.VISIBLE);
        });
    }

    /**
     * 显示错误信息并关闭页面
     * @param message 错误信息
     */
    private void showError(String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
