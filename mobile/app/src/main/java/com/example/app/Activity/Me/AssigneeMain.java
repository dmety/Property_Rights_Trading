package com.example.app.Activity.Me;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Activity.Me.Info.AssigneeMainDetails;
import com.example.app.Activity.Me.Info.AssigneeMainTransferee;
import com.example.app.Adapter.AssigneeMainAdapter;
import com.example.app.Model.AssigneeMainItem;
import com.example.app.Model.AssigneeMainResponse;
import com.example.testforenv.R;
import com.example.app.Utils.AssigneeMainApiUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * 主受让方管理Activity
 * 功能：展示项目列表，处理项目详情查看和受让流程启动
 */
public class AssigneeMain extends AppCompatActivity {
    // UI组件
    private RecyclerView recyclerView; // 项目列表RecyclerView
    private AssigneeMainAdapter adapter; // 列表适配器
    private List<AssigneeMainItem> itemList = new ArrayList<>(); // 数据源

    // 工具类
    private AssigneeMainApiUtils apiUtils; // 网络请求工具类

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assignee_main); // 设置布局文件

        // 初始化网络请求工具类
        apiUtils = AssigneeMainApiUtils.getInstance();

        // 初始化RecyclerView
        setupRecyclerView();

        // 加载项目数据
        loadProjects();
    }

    /**
     * 初始化RecyclerView及适配器
     */
    private void setupRecyclerView() {
        // 获取RecyclerView实例并设置布局管理器
        recyclerView = findViewById(R.id.assignee_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 创建适配器并设置点击监听
        adapter = new AssigneeMainAdapter(itemList, (position, action) -> {
            // 获取当前点击的项目
            AssigneeMainItem item = itemList.get(position);

            // 根据操作类型处理点击事件
            if (action == AssigneeMainAdapter.Action.DETAIL) {
                // 查看项目详情
                showProjectDetail(item.getRightNo());
            } else {
                // 启动受让流程
                startAssignmentProcess(item.getProjectCode());
            }
        });

        // 设置适配器
        recyclerView.setAdapter(adapter);
    }

    /**
     * 从服务器加载项目数据
     */
    private void loadProjects() {
        apiUtils.loadProjects(new AssigneeMainApiUtils.ProjectListCallback() {
            @Override
            public void onSuccess(AssigneeMainResponse response) {
                // 清空现有数据
                itemList.clear();

                // 遍历响应数据并填充到列表
                for (AssigneeMainResponse.DataBean project : response.getData()) {
                    itemList.add(new AssigneeMainItem(
                            project.getBaseInfo().getProjectCode(), // 项目编号
                            project.getBaseInfo().getRightNo(), // 权利编号
                            project.getBaseInfo().getProjectName(), // 项目名称
                            project.getUpInfo().getRollOutMode(), // 出让方式
                            project.getUpInfo().getUpPrice(), // 出让价格
                            project.getUpInfo().getUpPriceUnit() // 价格单位
                    ));
                }

                // 通知适配器数据变化
                runOnUiThread(() -> adapter.notifyDataSetChanged());
            }

            @Override
            public void onFailure(String errorMessage) {
                // 显示错误信息
                runOnUiThread(() ->
                        Toast.makeText(AssigneeMain.this, errorMessage, Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onError(Throwable throwable) {
                // 显示网络错误信息
                runOnUiThread(() ->
                        Toast.makeText(AssigneeMain.this, "网络错误: " + throwable.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
            }
        });
    }

    /**
     * 显示项目详情
     * @param rightNo 权利编号
     */
    private void showProjectDetail(String rightNo) {
        // 创建Intent并启动详情Activity
        Intent intent = new Intent(this, AssigneeMainDetails.class);
        intent.putExtra("RIGHT_NO", rightNo); // 传递权利编号参数
        startActivity(intent);
    }

    /**
     * 启动受让流程
     * @param projectCode 项目编号
     */
    private void startAssignmentProcess(String projectCode) {
        // 创建Intent并启动受让流程Activity
        Intent intent = new Intent(this, AssigneeMainTransferee.class);
        intent.putExtra("projectCode", projectCode); // 传递项目编号参数
        startActivity(intent);
    }
}
