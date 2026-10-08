package com.example.app;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.testforenv.R;
import com.example.app.Adapter.ImageAdapter;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1; // 权限请求码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 加载广告页面
        setContentView(R.layout.activity_advertise);
        setupBouncyCastle();

        // 检查并请求文件权限
        requestFilePermissions();

        viewPager = findViewById(R.id.viewPager);
        ImageAdapter imageAdapter = new ImageAdapter(getImages(),this);
        viewPager.setAdapter(imageAdapter);
    }

    // 请求文件权限
    private void requestFilePermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // 权限未被授予，申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    // 权限请求结果处理
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予
                Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                // 权限被拒绝
                Toast.makeText(this, "Permission denied! Cannot access files.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 显示图片
    private int[] getImages() {
        return new int[]{
                R.drawable.image1, // 第一张图片
                R.drawable.image2  // 第二张图片
        };
    }

    public void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
            // Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
            // BC with same package name, shouldn't happen in real life.
            return;
        }
        // Android registers its own BC provider. As it might be outdated and might not include
        // all needed ciphers, we substitute it with a known BC bundled in the app.
        // Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
        // of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
}
