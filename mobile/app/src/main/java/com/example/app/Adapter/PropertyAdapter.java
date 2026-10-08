package com.example.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.app.components.PropertyItem;
import com.example.testforenv.R;
import com.example.app.Activity.Me.Info.ProActivity;

import java.util.List;

public class PropertyAdapter extends ArrayAdapter<PropertyItem> {

    private List<PropertyItem> propertyList;

    public PropertyAdapter(Context context, List<PropertyItem> properties) {
        super(context, R.layout.list_item_property, properties);
        this.propertyList = properties;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 如果没有可重用的视图，则创建一个新的
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_property, parent, false);
        }

        // 获取当前物业项
        PropertyItem currentProperty = propertyList.get(position);

        // 设置标题和描述
        TextView titleTextView = convertView.findViewById(R.id.property_title);
        TextView descriptionTextView = convertView.findViewById(R.id.property_description);

        titleTextView.setText(currentProperty.getTitle());
        descriptionTextView.setText(currentProperty.getDescription());

        // 设置点击事件
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 启动 ProActivity，并传递项目编号
                Intent intent = new Intent(getContext(), ProActivity.class);
                intent.putExtra("project_id", currentProperty.getTitle()); // 假设项目编号是标题
                getContext().startActivity(intent);
            }
        });

        return convertView;
    }
}
