package com.example.app.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.testforenv.R;

public class CardItemView extends LinearLayout {

    private TextView titleTextView;
    private TextView descriptionTextView;
    private Button actionButton;

    public CardItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.card_item, this, true);
        titleTextView = findViewById(R.id.titleTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        actionButton = findViewById(R.id.actionButton);

        // 检查是否成功找到视图
        if (titleTextView == null || descriptionTextView == null || actionButton == null) {
            throw new NullPointerException("视图ID未找到，请检查布局文件的ID是否正确");
        }
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setDescription(String description) {
        descriptionTextView.setText(description);
    }

    public void setButtonClickListener(OnClickListener listener) {
        actionButton.setOnClickListener(listener);
    }
}
