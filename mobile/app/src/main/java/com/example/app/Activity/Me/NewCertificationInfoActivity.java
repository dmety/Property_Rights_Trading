package com.example.app.Activity.Me;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.app.Config.EnvConfig;
import com.example.testforenv.R;
import com.example.app.Utils.CertificateApiUtils;
import com.example.app.Utils.FileUtils;

import java.io.File;

/**
 * 新证书验证Activity
 * 功能：上传证书图片和编号进行真伪验证
 */
public class NewCertificationInfoActivity extends AppCompatActivity {

    // 请求码 - 选择图片
    private static final int PICK_IMAGE_REQUEST = 1;
    // 请求码 - 存储权限
    private static final int STORAGE_PERMISSION_CODE = 100;

    // UI控件
    private EditText certificateNumberInput;  // 证书编号输入框
    private ImageView uploadImagePreview;     // 图片预览
    private TextView fileNameText;            // 文件名显示
    private Button submitButton;              // 提交按钮
    private Uri selectedImageUri;             // 选择的图片URI

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_my_certificate);

        // 初始化UI控件
        certificateNumberInput = findViewById(R.id.certificateNumberInput);
        uploadImagePreview = findViewById(R.id.uploadImagePreview);
        fileNameText = findViewById(R.id.fileNameText);
        submitButton = findViewById(R.id.submitButton);

        // 上传按钮点击事件
        Button uploadButton = findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(v -> checkPermissionAndOpenFileChooser());

        // 提交按钮点击事件
        submitButton.setOnClickListener(v -> submitCertificate());
    }

    /**
     * 检查权限并打开文件选择器
     */
    private void checkPermissionAndOpenFileChooser() {
        // 检查是否有存储权限
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_CODE);
        } else {
            // 已有权限，直接打开文件选择器
            openFileChooser();
        }
    }

    /**
     * 打开文件选择器
     */
    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);  // 只显示可打开的文件
        intent.setType("image/*");  // 限定图片类型
        startActivityForResult(Intent.createChooser(intent, "选择图片"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 处理权限请求结果
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 权限授予，打开文件选择器
            openFileChooser();
        } else {
            // 权限被拒绝，提示用户
            Toast.makeText(this, "需要存储权限才能选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 处理文件选择结果
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // 获取持久化访问权限 (Android 10+重要步骤)
                getContentResolver().takePersistableUriPermission(
                        selectedImageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // 显示选择的图片
                uploadImagePreview.setVisibility(View.VISIBLE);
                fileNameText.setVisibility(View.VISIBLE);
                Glide.with(this).load(selectedImageUri).into(uploadImagePreview);
                fileNameText.setText(getFileName(selectedImageUri));
            }
        }
    }

    /**
     * 获取文件名
     * @param uri 文件URI
     * @return 文件名
     */
    private String getFileName(Uri uri) {
        return FileUtils.getFileName(this, uri);
    }

    /**
     * 提交证书验证
     */
    private void submitCertificate() {
        // 获取输入的证书编号
        String projectCode = certificateNumberInput.getText().toString().trim();

        // 验证输入
        if (TextUtils.isEmpty(projectCode)) {
            Toast.makeText(this, "请输入鉴证书编号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "请选择图片", Toast.LENGTH_SHORT).show();
            return;
        }

        // 设置按钮状态为"上传中"
        submitButton.setEnabled(false);
        submitButton.setText("上传中...");

        // 通过工具类进行证书验证
        CertificateApiUtils.verifyCertificate(this, selectedImageUri, projectCode,
                new CertificateApiUtils.VerificationCallback() {
                    @Override
                    public void onSuccess(CertificateApiUtils.ApiResponse response) {
                        runOnUiThread(() -> {
                            // 恢复按钮状态
                            submitButton.setEnabled(true);
                            submitButton.setText("确认提交");

                            // 根据验证结果显示不同提示
                            if (response.getSign() != null && response.getSign()) {
                                // 验证成功
                                showVerificationResultDialog(true, "该证书是真实的!");
                            } else {
                                // 验证失败
                                showVerificationResultDialog(false,
                                        TextUtils.isEmpty(response.getMessage()) ?
                                                "该证书是伪造的" : response.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            // 恢复按钮状态并显示错误
                            submitButton.setEnabled(true);
                            submitButton.setText("确认提交");
                            Log.d("验证状态:", errorMessage);
                            showVerificationResultDialog(false, "该证书是伪造的!");
                        });
                    }

                    @Override
                    public void onError(Throwable t) {
                        runOnUiThread(() -> {
                            // 恢复按钮状态并显示错误
                            submitButton.setEnabled(true);
                            submitButton.setText("确认提交");
                            showVerificationResultDialog(false,
                                    "发生错误: " + t.getMessage());
                            Log.e("CertificateVerification", "验证错误", t);
                        });
                    }
                });
    }

    /**
     * 显示验证结果
     * @param isAuthentic 是否验证通过
     * @param message 显示消息
     */
    private void showVerificationResultDialog(boolean isAuthentic, String message) {
        // 使用Toast显示结果
        Toast.makeText(this, message,
                isAuthentic ? Toast.LENGTH_LONG : Toast.LENGTH_LONG).show();
    }
}
