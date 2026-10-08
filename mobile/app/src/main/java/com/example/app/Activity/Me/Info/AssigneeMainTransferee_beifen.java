package com.example.app.Activity.Me.Info;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.Model.TransfereeModel;
import com.example.app.Utils.AssigneeMainApiUtils;
import com.example.app.Utils.NewHttpRequests;
import com.example.app.Utils.XunfeiSpeechUtil;
import com.example.testforenv.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 受让方信息填写页面Activity
 */
public class AssigneeMainTransferee_beifen extends AppCompatActivity {

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

    private XunfeiSpeechUtil speechHelper;
    private StringBuilder speechText = new StringBuilder();

    private NewHttpRequests httpRequests; // HTTP请求工具

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignee_transferee);

        projectCode = getIntent().getStringExtra("projectCode");
        transfereeModel = new TransfereeModel(projectCode);

        httpRequests = new NewHttpRequests(); // 初始化HTTP请求工具


        // 初始化语音工具
        speechHelper = new XunfeiSpeechUtil(this, new XunfeiSpeechUtil.SpeechCallback() {
            @Override
            public void onBegin() {
                runOnUiThread(() -> Toast.makeText(AssigneeMainTransferee_beifen.this, "请开始说话", Toast.LENGTH_SHORT).show());
                speechText.setLength(0);
            }
            @Override
            public void onEnd() { }
            @Override
            public void onResult(String text, boolean isLast) {
                Log.d("SPEECH", "识别返回: " + text + " | isLast=" + isLast);
                if (text != null) speechText.append(text);
                if (isLast) {
                    runOnUiThread(() -> {
                        if (!speechText.toString().isEmpty()) {
                            Log.d("语音识别结果", speechText.toString());
                            autoFillFormBySpeech(speechText.toString());
                            speechText.setLength(0); // 重置
                        }
                    });
                }
            }
            @Override
            public void onError(String error) {
                runOnUiThread(() -> Toast.makeText(AssigneeMainTransferee_beifen.this, "识别失败：" + error, Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onVolume(int volume) { /* 可做动画 */}
        });

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
            if (btnAiFill.getText().toString().equals("AI填报")) {
                btnAiFill.setText("录音中...");
                btnAiFill.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                speechHelper.startListening();
            } else {
                btnAiFill.setText("AI填报");
                btnAiFill.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                speechHelper.stopListening();
            }
        });

        btnApplyDate.setOnClickListener(v -> showDatePicker());
        btnSubmit.setOnClickListener(v -> submitForm());
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

    private void submitToServer() {
        AssigneeMainApiUtils.getInstance().submitTransferee(transfereeModel,
                new AssigneeMainApiUtils.AssigneeCallback() {
                    @Override
                    public void onSuccess(String message) {
                        runOnUiThread(() -> {
                            Toast.makeText(AssigneeMainTransferee_beifen.this, "受让方信息添加成功", Toast.LENGTH_SHORT).show();
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

    /**
     * 语音转文字自动填表
     */
    private void autoFillFormBySpeech(String aiContent) {
//        给出的AI内容进行格式化
        aiContent = "我把这段信息格式化，用json给我输入,输出样例：" +
                "{\"id\":\"156454651\",\"name\":\"王小明\",\"phone\":\"15651333444\",\"email\":\"wangxing@qq.com\n" + "\n" + "\",\"price\":\"456123\",\"unit\":\"元/亩\"}," +
                "你面对的可能是一个普通话不标准的用户，请通过这些文字可以进行一个猜测，然后把大写数字转为阿拉伯数字,返回的key值必须严格按照我给的样例:"+aiContent;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechHelper != null) speechHelper.destroy();
    }
}
