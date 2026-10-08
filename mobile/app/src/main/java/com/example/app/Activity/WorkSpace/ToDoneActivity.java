package com.example.app.Activity.WorkSpace;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import com.example.testforenv.R;
import com.example.app.components.FieldItemView;

public class ToDoneActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workspace_todone);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 隐藏状态栏
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        ImageButton btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 结束当前活动，返回上一个活动
            }
        });

        // 获取 FieldItemView 实例并设置字段
        FieldItemView fieldProjectNumber = findViewById(R.id.field_project_number);
        fieldProjectNumber.setField("项目编号:", "XM2024A00001");

        FieldItemView fieldProjectName = findViewById(R.id.field_project_name);
        fieldProjectName.setField("项目名称:", "XX1市XX1区XX1街高中村530.64亩土地经营权");

        FieldItemView fieldTransferType = findViewById(R.id.field_transfer_type);
        fieldTransferType.setField("转让类型:", "农村土地承包经营权");

        FieldItemView fieldRightNumber = findViewById(R.id.field_right_number);
        fieldRightNumber.setField("权利编号:", "CQ2024A00001");

        FieldItemView fieldRightName = findViewById(R.id.field_right_name);
        fieldRightName.setField("权利名称:", "XX1市XX1区XX1街高中村530.64亩土地");

        FieldItemView fieldCertificateCode = findViewById(R.id.field_certificate_code);
        fieldCertificateCode.setField("权证代码:", "QZ2024B00001");

        FieldItemView fieldOwnerShip = findViewById(R.id.field_ownership);
        fieldOwnerShip.setField("所有权:", "集体经济组织");

        FieldItemView fieldLandArea = findViewById(R.id.field_land_area);
        fieldLandArea.setField("土地面积:", "100亩");

        FieldItemView fieldLandNature = findViewById(R.id.field_land_nature);
        fieldLandNature.setField("土地性质:", "耕地");

        FieldItemView fieldTransferor = findViewById(R.id.field_transferor);
        fieldTransferor.setField("转让人:", "张X兰");

        FieldItemView fieldTransferee = findViewById(R.id.field_transferee);
        fieldTransferee.setField("受让人:", "王*冰");

        FieldItemView fieldAptDate = findViewById(R.id.field_apt_date);
        fieldAptDate.setField("受理日期:", "2024-10-05");

        FieldItemView fieldProjectStep = findViewById(R.id.field_project_step);
        fieldProjectStep.setField("项目进度:", "成交公告");

        FieldItemView fieldUpStatus = findViewById(R.id.field_up_status);
        fieldUpStatus.setField("挂牌状态:", "首次挂牌中");
    }
}
