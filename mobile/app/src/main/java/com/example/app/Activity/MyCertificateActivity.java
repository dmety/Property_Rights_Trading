package com.example.app.Activity;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.testforenv.R;

public class MyCertificateActivity extends AppCompatActivity {
    ImageButton btn_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_certificate);

        btn_back = findViewById(R.id.btn_back);
        btn_back.setOnClickListener(v -> finish());
    }
}
