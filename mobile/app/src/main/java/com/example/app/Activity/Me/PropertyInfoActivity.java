package com.example.app.Activity.Me;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.app.Adapter.PropertyAdapter;
import com.example.app.components.PropertyItem;
import com.example.testforenv.R;

import java.util.ArrayList;
import java.util.List;

public class PropertyInfoActivity extends AppCompatActivity {

    private ListView listView;
    private PropertyAdapter adapter; // 使用 PropertyAdapter
    private List<PropertyItem> propertyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_list);

        listView = findViewById(R.id.info_list);

        // 初始化物业数据
        propertyList = new ArrayList<>();
        loadPropertyData();

        // 初始化适配器
        adapter = new PropertyAdapter(this, propertyList);
        listView.setAdapter(adapter); // 设置适配器
    }

    private void loadPropertyData() {
        // 添加示例数据
        propertyList.add(new PropertyItem("420130004Q24090001", "xx区xx街八一村30亩土地出租 - 标段1"));
        propertyList.add(new PropertyItem("420116009A24090002", "xxxxxx街道高潮村武湖渔池危桥改造工程 - 标段1。"));
    }
}
