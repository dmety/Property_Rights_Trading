package com.example.app.Activity.Me.Info;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.app.Model.TransfereeModel;
import com.example.app.Utils.NewHttpRequests;
import com.example.app.Utils.WavRecorderUtil;
import com.example.testforenv.R;
import com.example.app.Utils.AssigneeMainApiUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import com.example.app.Utils.XunfeiSpeechUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 受让方信息填写页面Activity
 */
public class AssigneeMainTransferee extends AppCompatActivity {

    // UI组件声明
    private RadioGroup rgUserType;
    private EditText etTransfereeName, etEmail, etCardNo, etTelephone, etRegisteredCapital, etLegalRep, etCompanyName, etWillPrice;
    private Spinner spCardType, spPriceUnit;
    private Button btnApplyDate, btnAiFill, btnSubmit;
    private LinearLayout layoutLegalInfo, layoutNaturalInfo;

    // 数据模型与工具
    private TransfereeModel transfereeModel;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String projectCode;

    private WavRecorderUtil wavRecorder;

    private NewHttpRequests httpRequests; // HTTP请求工具

    private ProgressBar loadingBar; // 你可以在布局里放一个ProgressBar，初始隐藏，转圈图标
    private View loadingMask; // 加载中背景半透明

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignee_transferee);

        projectCode = getIntent().getStringExtra("projectCode");
        transfereeModel = new TransfereeModel(projectCode);

        httpRequests = new NewHttpRequests(); // 初始化HTTP请求工具

        wavRecorder = new WavRecorderUtil(this);

        loadingBar = findViewById(R.id.progress_bar); // 假设你的布局里有
        loadingMask = findViewById(R.id.loading_mask);

        initViews();
        setupListeners();

    }

    private void initViews() {
        rgUserType = findViewById(R.id.rg_user_type);
        etTransfereeName = findViewById(R.id.et_transferor_name);
        btnApplyDate = findViewById(R.id.btn_apply_date);
        etEmail = findViewById(R.id.et_email);
        etCardNo = findViewById(R.id.et_card_no);
        etTelephone = findViewById(R.id.et_telephone);
        layoutLegalInfo = findViewById(R.id.layout_legal_info);
        layoutNaturalInfo = findViewById(R.id.layout_natural_info);
        etRegisteredCapital = findViewById(R.id.et_registered_capital);
        etLegalRep = findViewById(R.id.et_legal_representative);
        etCompanyName = findViewById(R.id.et_company_name);
        spCardType = findViewById(R.id.sp_card_type);
        etWillPrice = findViewById(R.id.et_will_price);
        spPriceUnit = findViewById(R.id.sp_price_unit);
        btnSubmit = findViewById(R.id.btn_submit);
        btnAiFill = findViewById(R.id.btn_ai_fill);

        etEmail.setText("无");
        spPriceUnit.setSelection(0);
        etWillPrice.setText("1000");
    }

    private void setupListeners() {
        rgUserType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rb_natural) {
                transfereeModel.setUserType("自然人");
                layoutNaturalInfo.setVisibility(View.VISIBLE);
                layoutLegalInfo.setVisibility(View.GONE);
                spCardType.setSelection(0);
            } else if (checkedId == R.id.rb_legal) {
                transfereeModel.setUserType("法人");
                layoutLegalInfo.setVisibility(View.VISIBLE);
                layoutNaturalInfo.setVisibility(View.GONE);
            } else {
                transfereeModel.setUserType("其他组织");
                layoutLegalInfo.setVisibility(View.GONE);
                layoutNaturalInfo.setVisibility(View.GONE);
            }
        });

        // AI填报按钮集成语音识别
        btnAiFill.setOnClickListener(v -> {
            if (!wavRecorder.isRecording()) {
                // 1. 先检查权限
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    // 2. 没权限，请求授权
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_RECORD_AUDIO_PERMISSION);
                    // return 等待用户操作授权弹窗
                    return;
                }
                // 3. 已授权，直接开录音
                startRecordingWithUi();
            } else {
                btnAiFill.setText("AI填报");
                btnAiFill.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                wavRecorder.stopRecording();
                // 停止录音发送语言流
                new android.os.Handler().postDelayed(() -> {
                    uploadAudioFile(wavRecorder.getCurrentFilePath());
                }, 150);
            }
        });

        btnApplyDate.setOnClickListener(v -> showDatePicker());
        btnSubmit.setOnClickListener(v -> submitForm());
    }
//  开始录音
    private void startRecordingWithUi() {
        try {
            btnAiFill.setText("停止录音");
            btnAiFill.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            wavRecorder.startRecording();
            Toast.makeText(this, "正在录音...", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "录音启动失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void uploadAudioFile(String filePath) {
        showLoading(true);

        File wavFile = new File(filePath);
//        发送请求
        httpRequests.uploadToAsrService(wavFile, new NewHttpRequests.AsrCallback() {
            @Override
            public void onSuccess(String recognizedText) {
                runOnUiThread(() -> {
                    showLoading(false);
                    // 根据识别文本自动填充表单
                    try {
                        processRecognitionResult(recognizedText);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    showLoading(false);
                    Toast.makeText(AssigneeMainTransferee.this,
                            "语音识别失败: " + errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    // 加入showLoading方法
    private void showLoading(boolean show) {
        if (loadingBar != null && loadingMask != null) {
            loadingBar.setVisibility(show ? View.VISIBLE : View.GONE);
            loadingMask.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, day) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year, month, day);
            String dateStr = dateFormat.format(selectedDate.getTime());
            btnApplyDate.setText(dateStr);
            transfereeModel.setApplyDate(dateStr);
        },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void submitForm() {
        if (!validateForm()) return;

        transfereeModel.setTransfereeName(etTransfereeName.getText().toString());
        transfereeModel.setEmail(etEmail.getText().toString());
        transfereeModel.setTransfereeCardNo(etCardNo.getText().toString());
        transfereeModel.setTelephone(etTelephone.getText().toString());
        transfereeModel.setWillPrice(etWillPrice.getText().toString());
        transfereeModel.setWillPriceUnit(spPriceUnit.getSelectedItem().toString());

        if (transfereeModel.getUserType().equals("法人")) {
            transfereeModel.setRegisteredCapital(etRegisteredCapital.getText().toString());
            transfereeModel.setLegalRepresentative(etLegalRep.getText().toString());
            transfereeModel.setOrganRegNo(etCompanyName.getText().toString());
        } else if (transfereeModel.getUserType().equals("自然人")) {
            transfereeModel.setCardType(spCardType.getSelectedItem().toString());
        }

        transfereeModel.setOrganRegNo("");  // 保证非空

        submitToServer();
    }

    private boolean validateForm() {
        if (etTransfereeName.getText().toString().isEmpty()) {
            showError("请填写受让方名称"); return false;
        }
        if (btnApplyDate.getText().toString().equals("选择申请时间")) {
            showError("请选择申请时间"); return false;
        }
        if (etCardNo.getText().toString().isEmpty()) {
            showError("请填写证件号码"); return false;
        }
        if (etTelephone.getText().toString().isEmpty()) {
            showError("请填写联系电话"); return false;
        }
        if (etWillPrice.getText().toString().isEmpty()) {
            showError("请填写意向价格"); return false;
        }
        if (transfereeModel.getUserType().equals("法人") &&
                etRegisteredCapital.getText().toString().isEmpty()) {
            showError("请填写注册资本"); return false;
        }
        return true;
    }

    /**
     * 添加受让方信息
     * */
    private void submitToServer() {
        AssigneeMainApiUtils.getInstance().submitTransferee(transfereeModel,
                new AssigneeMainApiUtils.AssigneeCallback() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> {
                            Toast.makeText(AssigneeMainTransferee.this, "受让方信息添加成功", Toast.LENGTH_SHORT).show();
                            finish();
                        });
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> showError("受让方信息添加失败: " + errorMessage));
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> showError("网络错误: " + throwable.getMessage()));
                    }
                });
    }


    /*
    * 自动填表
    * */
    private void processRecognitionResult(String recognizedText) throws JSONException {
        JSONObject jsonObject = new JSONObject(prepareSpeechText(recognizedText));
        Log.d("AI回复", jsonObject.toString());
//                 将获取到的数据填入表单
        String name = jsonObject.optString("name", "");
        String id = jsonObject.optString("id", "");
        String phone = jsonObject.optString("phone", "");
        String email = jsonObject.optString("email", "");
        String price = jsonObject.optString("price", "");
        String unit = jsonObject.optString("unit", "");
        runOnUiThread(() -> {
            if (!name.isEmpty()) etTransfereeName.setText(name);
            if (!id.isEmpty()) etCardNo.setText(id);
            if (!phone.isEmpty()) etTelephone.setText(phone);
            if (!email.isEmpty()) etEmail.setText(email);
            if (!price.isEmpty()) etWillPrice.setText(price);
            if (unit.equals("元")) {
                spPriceUnit.setSelection(0);
            } else {
                spPriceUnit.setSelection(1);
            }
            // 默认填今天
            String today = dateFormat.format(Calendar.getInstance().getTime());
            btnApplyDate.setText(today);
            transfereeModel.setApplyDate(today);
        });
    }

    /**
     * 语音转文字自动填表
     */
    private void autoFillFormBySpeech(String aiContent) {
//        发送http请求，将aiContent作为参数发送给后端
        // 获取AI回复
        httpRequests.getAiResponse(aiContent, new NewHttpRequests.AiResponseCallback() {
            @Override
            public void onResponse(String aiResponse) throws JSONException {
                JSONObject jsonObject = new JSONObject(prepareSpeechText(aiResponse));
                Log.d("AI回复", jsonObject.toString());
//                 将获取到的数据填入表单
                String name = jsonObject.optString("name", "");
                String id = jsonObject.optString("id", "");
                String phone = jsonObject.optString("phone", "");
                String email = jsonObject.optString("email", "");
                String price = jsonObject.optString("price", "");
                String unit = jsonObject.optString("unit", "");
                runOnUiThread(() -> {
                    if (!name.isEmpty()) etTransfereeName.setText(name);
                    if (!id.isEmpty()) etCardNo.setText(id);
                    if (!phone.isEmpty()) etTelephone.setText(phone);
                    if (!email.isEmpty()) etEmail.setText(email);
                    if (!price.isEmpty()) etWillPrice.setText(price);
                    if (unit.equals("元")) {
                        spPriceUnit.setSelection(0);
                    } else {
                        spPriceUnit.setSelection(1);
                    }
                    // 默认填今天
                    String today = dateFormat.format(Calendar.getInstance().getTime());
                    btnApplyDate.setText(today);
                    transfereeModel.setApplyDate(today);
                });
            }

            @Override
            public void onError(String errorMessage) {


            }
        });
    }


    /*
    * 清除ai回答的思考内容，只保留需要内容
    * */

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

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
