package com.example.app.Activity.Me.Info;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.testforenv.R;

public class ImageViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_me_certification_image_info);

        PhotoView fullImageView = findViewById(R.id.fullImageView);
        Button downloadButton = findViewById(R.id.downloadButton);

        // 获取传递的图片 URL
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // 加载图片
        Glide.with(this).load(imageUrl).into(fullImageView);

        // 设置下载按钮点击事件
        downloadButton.setOnClickListener(v -> downloadImage(imageUrl));
    }

    private void downloadImage(String imageUrl) {
        // 下载图片的逻辑，可以使用下载管理器或其他方式
        Uri uri = Uri.parse(imageUrl);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }
}
