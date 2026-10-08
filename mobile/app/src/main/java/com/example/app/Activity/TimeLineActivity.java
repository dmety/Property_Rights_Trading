package com.example.app.Activity; // 请替换为您的包名

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testforenv.R;
import com.example.app.Adapter.TimelineAdapter;

public class TimeLineActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TimelineAdapter timelineAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line); // 确保使用您创建的布局文件

//        // 初始化RecyclerView
//        recyclerView = findViewById(R.id.recycler_view); // 确保您在布局文件中添加了RecyclerView
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//
//        // 创建数据列表
//        List<TimelineItem> items = new ArrayList<>();
//        items.add(new TimelineItem("标题1", "内容1", "时间1"));
//        items.add(new TimelineItem("标题2", "内容2", "时间2"));
//        items.add(new TimelineItem("标题3", "内容3", "时间3"));
//        items.add(new TimelineItem("标题4", "内容4", "时间4"));
//        items.add(new TimelineItem("标题5", "内容5", "时间5"));
//
//        // 设置适配器
//        timelineAdapter = new TimelineAdapter(items);
//        recyclerView.setAdapter(timelineAdapter);
    }
}
