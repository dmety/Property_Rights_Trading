package com.example.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.example.testforenv.R;
import com.example.app.Config.EnvConfig;
import com.example.app.Model.ProjectBaseInfo;
import com.example.app.Utils.OkHttpUtil;
import com.example.app.components.FieldItemView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ProjectInfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_info);

        // 获取传递进来的ProjectCode
        Intent intent = getIntent();
        String projectId = intent.getStringExtra("PROJECT_ID");

        // 获取 FieldItemView 实例
        FieldItemView fieldProjectNumber = findViewById(R.id.field_project_number);
        FieldItemView fieldProjectName = findViewById(R.id.field_project_name);
        FieldItemView fieldTransferType = findViewById(R.id.field_transfer_type);
        FieldItemView fieldRightNumber = findViewById(R.id.field_right_number);
        FieldItemView fieldRightName = findViewById(R.id.field_right_name);
        FieldItemView fieldCertificateCode = findViewById(R.id.field_certificate_code);
        FieldItemView fieldOwnerShip = findViewById(R.id.field_ownership);
        FieldItemView fieldLandArea = findViewById(R.id.field_land_area);
        FieldItemView fieldLandNature = findViewById(R.id.field_land_nature);
        FieldItemView fieldTransferor = findViewById(R.id.field_transferor);
        FieldItemView fieldTransferee = findViewById(R.id.field_transferee);
        FieldItemView fieldAptDate = findViewById(R.id.field_apt_date);
        FieldItemView fieldProjectStep = findViewById(R.id.field_project_step);
        FieldItemView fieldUpStatus = findViewById(R.id.field_up_status);

        // 根据项目编号获取详细信息
        OkHttpUtil.sendGetRequest(EnvConfig.PROJECT_INFO_BY_PROJECT_CODE+"?projectCode="+projectId, new OkHttpUtil.OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) throws JSONException {
                JSONObject result = new JSONObject(response);
                JSONObject baseInfo = result.getJSONObject("data").getJSONObject("baseInfo");
                Log.d("getProjectInfoByProjectResult ===>" ,result.toString());

                // 更新UI，确保在主线程中运行
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // 更新每个 FieldItemView 的值
                        try {
                            fieldProjectNumber.setField("项目编号", baseInfo.getString("projectCode"));
                            fieldProjectName.setField("项目名称", baseInfo.getString("projectName"));
                            fieldTransferType.setField("转让类型", baseInfo.getString("transKind"));
                            fieldRightNumber.setField("权利编号", baseInfo.getString("rightNo"));
                            fieldRightName.setField("权利名称", baseInfo.getString("projectName"));
                            fieldCertificateCode.setField("权证代码", "QZ2024B00001");  // 假设这个值固定
                            fieldOwnerShip.setField("所有权", baseInfo.getString("orgName"));
                            fieldLandArea.setField("土地面积", "100亩");  // 假设固定
                            fieldLandNature.setField("土地性质", "耕地");  // 假设固定
                            fieldTransferor.setField("转让人", baseInfo.getString("userName"));
                            fieldTransferee.setField("受让人", "王*冰");  // 假设固定
                            fieldAptDate.setField("受理日期", baseInfo.getString("aptDate"));
                            fieldProjectStep.setField("项目进度", baseInfo.getString("projectStatus"));
                            fieldUpStatus.setField("挂牌状态", "首次挂牌中");  // 假设固定
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });

                return null;
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("ProjectInfoActivity", "Request failed", e);
            }

            @Override
            public void onError(String error) {
                Log.e("ProjectInfoActivity", "Error: " + error);
            }
        });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 结束当前活动，返回上一个活动
            }
        });

        // 隐藏状态栏
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

}
