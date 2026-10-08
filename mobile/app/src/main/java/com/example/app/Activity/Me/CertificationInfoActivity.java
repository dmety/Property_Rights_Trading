package com.example.app.Activity.Me;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.example.testforenv.R;
import com.example.app.Activity.Me.Info.ImageViewActivity;

import java.util.Arrays;
import java.util.List;

public class CertificationInfoActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certification_list);

        // 检查存储权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            loadCertificates(); // 权限已授予，加载证书
        }
    }

    private void loadCertificates() {
        List<String> certificateImages = Arrays.asList(
                "/storage/emulated/0/test/book1.png",
                "/storage/emulated/0/test/book1.png",
                "/storage/emulated/0/test/book1.png",
                "/storage/emulated/0/test/book1.png",
                "/storage/emulated/0/test/book1.png"
        );

        LinearLayout certificatesContainer = findViewById(R.id.certificatesContainer);

        for (String imageUrl : certificateImages) {
            View cardView = getLayoutInflater().inflate(R.layout.card_me_certification_image, certificatesContainer, false);

            ImageView cardImage = cardView.findViewById(R.id.cardImage);
            Glide.with(this).load(imageUrl).into(cardImage);

            // 添加点击事件
            cardImage.setOnClickListener(v -> {
                Intent intent = new Intent(CertificationInfoActivity.this, ImageViewActivity.class);
                intent.putExtra("imageUrl", imageUrl);
                startActivity(intent);
            });

            certificatesContainer.addView(cardView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadCertificates(); // 权限被授予，加载证书
            } else {
                // 权限被拒绝，您可以在此处处理用户未授权的情况
            }
        }
    }
}
