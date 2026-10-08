package com.example.app.components; // 请替换为您的包名

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.testforenv.R;

public class TimelineItemView extends LinearLayout {

    private TextView titleTextView;
    private TextView contentTextView;
    private TextView timeTextView;

    public TimelineItemView(Context context) {
        super(context);
        init(context);
    }

    public TimelineItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TimelineItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.time_line, this, true);
        titleTextView = findViewById(R.id.textView1);
        contentTextView = findViewById(R.id.textView2);
        timeTextView = findViewById(R.id.textView3);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setContent(String content) {
        contentTextView.setText(content);
    }

    public void setTime(String time) {
        timeTextView.setText(time);
    }
}
