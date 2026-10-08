package com.example.app.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.testforenv.R;

public class FieldItemView extends LinearLayout {
    private TextView fieldName;
    private TextView fieldValue;

    public FieldItemView(Context context) {
        super(context);
        init(context);
    }

    public FieldItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FieldItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.component_table_line, this, true);
        fieldName = findViewById(R.id.field_name);
        fieldValue = findViewById(R.id.field_value);
    }

    public void setField(String name, String value) {
        fieldName.setText(name);
        fieldValue.setText(value);
    }
}
