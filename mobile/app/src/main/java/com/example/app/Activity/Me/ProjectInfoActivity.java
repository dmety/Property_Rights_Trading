package com.example.app.Activity.Me;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Adapter.ProjectAdapter;
import com.example.app.components.ProjectItem;
import com.example.testforenv.R;

import java.util.ArrayList;
import java.util.List;

public class ProjectInfoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProjectAdapter adapter;
    private List<ProjectItem> projectList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 初始化项目数据
        projectList = new ArrayList<>();
        loadProjectData();

        // 初始化适配器
        adapter = new ProjectAdapter(projectList);
        recyclerView.setAdapter(adapter);
    }

    private void loadProjectData() {
        // 添加示例数据
        projectList.add(new ProjectItem("420130004Q24090001", "江夏区安山街八一村30亩土地出租 - 标段1"));
        projectList.add(new ProjectItem("420116009A24090002", "长江新区阳逻街道高潮村武湖渔池危桥改造工程 - 标段1。"));


    }
}
