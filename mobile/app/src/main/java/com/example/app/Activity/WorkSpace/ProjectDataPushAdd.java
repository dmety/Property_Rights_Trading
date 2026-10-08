package com.example.app.Activity.WorkSpace;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Adapter.CollectionPointAdapter;
import com.example.app.Model.CollectionPoint;
import com.example.app.Model.Device;
import com.example.app.Model.DeviceData;
import com.example.app.Model.Result;
import com.example.app.Utils.ProjectDataPushAddApiUtils;
import com.example.testforenv.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目数据采集与提交界面
 * 主要功能：
 * 1. 显示项目基本信息（编号、面积、周长）
 * 2. 管理多个采集点（增删改查）
 * 3. 连接采集设备执行数据采集
 * 4. 提交所有采集数据.
 */
public class ProjectDataPushAdd extends AppCompatActivity {
    private final String TAG = "ProjectDataPushAdd";

    // === UI组件 ===
    private TextView tvProjectCode;       // 项目编号显示
    private TextView tvArea;              // 土地面积显示
    private TextView tvPerimeter;         // 土地周长显示
    private Button btnAddCollectionPoint; // 添加采集点按钮
    private Button btnConfirm;            // 确认提交按钮
    private Button btnRefresh;            // 刷新数据按钮
    private RecyclerView rvCollectionPoints; // 采集点列表容器

    // === 数据 ===
    private String projectCode;           // 当前项目编号
    private CollectionPointAdapter adapter; // 采集点列表适配器
    private final List<CollectionPoint> collectionPoints = new ArrayList<>(); // 采集点数据集合

    private String user; // 当前用户名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.active_project_data_push_add);

        // 初始化方法调用链
        initViews();      // 初始化界面元素
        initListeners();  // 设置事件监听
        loadInitialData();// 加载初始数据

//        获取当前登录信息（主要为用户）

        SharedPreferences sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        // 获取 UserInfo
        JSONObject userInfo = null;
        JSONObject credentialSubject = null;
        try {
            // 获取userinfo 对象
            userInfo = new JSONObject(sharedPreferences.getString("userInfo", "{}"));
            // 先获取 credentialSubject 对象
            credentialSubject = userInfo.getJSONObject("credentialSubject");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // 获取 userName
        user = credentialSubject.optString("userName","用户123");
    }

    /**
     * 初始化所有界面组件
     */
    private void initViews() {
        // 绑定布局文件中的视图组件
        tvProjectCode = findViewById(R.id.tv_project_code);
        tvArea = findViewById(R.id.tv_area);
        tvPerimeter = findViewById(R.id.tv_perimeter);
        btnAddCollectionPoint = findViewById(R.id.btn_add_collection_point);
        btnConfirm = findViewById(R.id.btn_confirm);
        btnRefresh = findViewById(R.id.btn_refresh);

        // 初始化RecyclerView及其适配器
        rvCollectionPoints = findViewById(R.id.rv_collection_points);
        rvCollectionPoints.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CollectionPointAdapter(collectionPoints, rvCollectionPoints);

        // 设置采集点设备选择监听器
        adapter.setDeviceSelectionListener(new CollectionPointAdapter.DeviceSelectionListener() {
            @Override
            public void onDevicesRequested(CollectionPoint point, int position) {
                // 当需要获取设备列表时触发
                fetchAvailableDevices(point, position);
            }

            @Override
            public void onCollectionCommandSent(CollectionPoint point, String deviceName, int position) {
                // 当发送采集指令时触发
                executeCollectionCommand(point, deviceName, position);
            }
        });

        rvCollectionPoints.setAdapter(adapter);
    }

    /**
     * 初始化所有按钮的点击事件
     */
    private void initListeners() {
        // 添加采集点按钮
        btnAddCollectionPoint.setOnClickListener(v -> addNewCollectionPoint());

        // 确认提交按钮
        btnConfirm.setOnClickListener(v -> validateAndSubmit());

        // 刷新数据按钮
        btnRefresh.setOnClickListener(v -> refreshLandData());
    }

    /**
     * 加载初始项目数据
     */
    private void loadInitialData() {
        // 从Intent获取项目编号
        projectCode = getIntent().getStringExtra("PROJECT_CODE");

        // 校验项目编号有效性
        if (projectCode == null || projectCode.isEmpty()) {
            Toast.makeText(this, "未获取到项目编号", Toast.LENGTH_SHORT).show();
            finish(); // 关闭当前Activity
            return;
        }

        // 显示项目编号
        tvProjectCode.setText("项目编号: " + projectCode);

        // 加载土地数据
        refreshLandData();

        // 加载初始采集点数据（演示用）
        loadCollectionPoints();
    }

    /**
     * 添加新的采集点到列表 （已完成）
     */
    private void addNewCollectionPoint() {
//        String pointName = String.format("采集点%d", collectionPoints.size() + 1);
        String pointName;
//        获取新的采集点名称
        if (collectionPoints.isEmpty()){
            pointName = "采集点1";
        }else {
            String pointNameMax = collectionPoints.get(collectionPoints.size()-1).getPointName();
            int count = Integer.parseInt(pointNameMax.substring(3));
            pointName = String.format("采集点%d",count+1);
        }
                // 显示加载状态
        Toast.makeText(this, "正在添加采集点...", Toast.LENGTH_SHORT).show();
        // 调用API添加采集点
        ProjectDataPushAddApiUtils.getInstance().addNewCollectionPoint(
                pointName, projectCode,
                new ProjectDataPushAddApiUtils.CollectionPointCallback() {
                    @Override
                    public void onSuccess(Result<String> result) {
                        runOnUiThread(() -> {
                            // 创建新采集点对象
                            CollectionPoint newPoint = new CollectionPoint(
                                    pointName,
                                    "---",  // 氮含量
                                    "---",  // 磷含量
                                    "---",  // 钾含量
                                    "未采集"  // 状态
                            );

                            collectionPoints.add(newPoint);
                            adapter.notifyItemInserted(collectionPoints.size() - 1);
                            rvCollectionPoints.smoothScrollToPosition(collectionPoints.size() - 1);

                            Toast.makeText(ProjectDataPushAdd.this,
                                    "采集点添加成功", Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override
                    public void onFailure(Result<String> result) {
                        runOnUiThread(() ->
                                Toast.makeText(ProjectDataPushAdd.this,
                                        "添加失败: " + result.getMessage(),
                                        Toast.LENGTH_SHORT).show()
                        );
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() ->
                                Toast.makeText(ProjectDataPushAdd.this,
                                        "网络错误: " + throwable.getMessage(),
                                        Toast.LENGTH_SHORT).show()
                        );
                    }
                }
        );
    }

    // =============== 土地数据相关方法 ===============

    /**
     * 刷新土地面积和周长数据（模拟网络请求）
     */
    private void refreshLandData() {
        loadCollectionPoints();
        // 使用Handler模拟网络延迟
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // 更新UI显示（模拟数据）
            tvArea.setText("---亩");
            tvPerimeter.setText("---米");
            Toast.makeText(this, "土地数据已刷新", Toast.LENGTH_SHORT).show();
        }, 800);
    }

    // =============== 采集点数据相关方法 ===============

    /**
     * 从服务器加载采集点数据（已完成）
     */
    private void loadCollectionPoints() {
        // 显示加载状态
        Toast.makeText(this, "正在加载采集点数据...", Toast.LENGTH_SHORT).show();
        // 调用API获取采集点
        ProjectDataPushAddApiUtils.getInstance().getCollectionPoints(
                projectCode,
                new ProjectDataPushAddApiUtils.CollectionPointsCallback() {
                    @Override
                    public void onSuccess(Result<List<DeviceData>> result) {
                        runOnUiThread(() -> {
                            List<DeviceData> deviceDataList = result.getData();
                            collectionPoints.clear();

                            for (DeviceData deviceData : deviceDataList) {
                                String n = "---";
                                String p = "---";
                                String k = "---";
                                // 分割数据，注意判空
                                String dataValue = deviceData.getDataValue();
                                if (dataValue != null && dataValue.contains("|")) {
                                    String[] arr = dataValue.split("\\|");
                                    if (arr.length >= 3) {
                                        n = arr[0];
                                        p = arr[1];
                                        k = arr[2];
                                    } else {
                                        // 数据异常时做兼容
                                        if (arr.length > 0) n = arr[0];
                                        if (arr.length > 1) p = arr[1];
                                        // k 如果没有就保持---
                                    }
                                }

                                CollectionPoint point = new CollectionPoint(
                                        deviceData.getAcquisitionPoint(),
                                        n,  // 氮含量
                                        p,  // 磷含量
                                        k,  // 钾含量
                                        deviceData.getCollectTime() == null ? "未采集" : "已采集"
                                );
                                collectionPoints.add(point);
                            }

                            adapter.notifyDataSetChanged();
                            Toast.makeText(ProjectDataPushAdd.this,
                                    "成功加载 " + collectionPoints.size() + " 个采集点",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override
                    public void onFailure(Result<List<DeviceData>> result) {
                        runOnUiThread(() -> {
                            Toast.makeText(ProjectDataPushAdd.this,
                                    "加载失败: " + result.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            // 失败时加载本地缓存或空数据
                            collectionPoints.clear();
                            adapter.notifyDataSetChanged();
                        });
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> {
                            Toast.makeText(ProjectDataPushAdd.this,
                                    "网络错误: " + throwable.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            // 错误时加载本地缓存或空数据
                            collectionPoints.clear();
                            adapter.notifyDataSetChanged();
                        });
                    }
                }
        );
    }

    /**
     * 从API获取可用设备列表 (已完成)
     * @param point 当前采集点
     * @param position 在列表中的位置
     */
    private void fetchAvailableDevices(CollectionPoint point, int position) {
        Log.d(TAG, "正在为位置 " + position + " 获取设备...");
        // 显示加载状态
        Toast.makeText(this, point.getPointName() + " 正在获取设备列表...",
                Toast.LENGTH_SHORT).show();
        // 获取当前用户名（替换为实际获取逻辑）
        ProjectDataPushAddApiUtils.getInstance().getAvailableDevices(
                user,
                new ProjectDataPushAddApiUtils.DeviceListCallback() {
                    @Override
                    public void onSuccess(Result<List<Device>> result) {
                        runOnUiThread(() -> {


                            List<Device> devices = result.getData();

                            // 更新对应位置的设备列表
                            adapter.updateDevicesForPosition(position, devices);

                            Log.d(TAG, "位置 " + position + " 设备列表已更新");
                            Toast.makeText(ProjectDataPushAdd.this,
                                    "获取到 " + devices.size() + " 个设备",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override
                    public void onFailure(Result<List<Device>> result) {
                        runOnUiThread(() -> {
                            Log.e(TAG, "获取设备失败: " + result.getMessage());
                            Toast.makeText(ProjectDataPushAdd.this,
                                    "获取设备失败: " + result.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> {
                            Log.e(TAG, "设备请求错误: ", throwable);
                            Toast.makeText(ProjectDataPushAdd.this,
                                    "网络错误: " + throwable.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                }
        );
    }

    /**
     * 执行采集指令
     * @param point 目标采集点
     * @param deviceName 使用的设备名称
     * @param position 在列表中的位置
     */
    private void executeCollectionCommand(CollectionPoint point, String deviceName, int position) {
        if (deviceName == null || deviceName.equals("选择设备") || deviceName.equals("无可用设备")) {
            Toast.makeText(this, "请先选择有效设备", Toast.LENGTH_SHORT).show();
            return;
        }
        // 获取设备ID（从CollectionPoint中获取已选设备ID）
        long deviceId = point.getSelectedDeviceId();
        if (deviceId == -1) {
            Toast.makeText(this, "设备ID无效", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示开始采集状态
        Log.d(TAG, "开始采集，设备ID: " + deviceId + "，采集点: " + point.getPointName());
        Toast.makeText(this, point.getPointName() + " 启动采集中...", Toast.LENGTH_SHORT).show();
        // 禁用对应位置的控件避免重复点击
        adapter.disableItemAtPosition(position);
        // 调用API开始采集（注意修改为使用deviceId而不是deviceName）
        ProjectDataPushAddApiUtils.getInstance().startCollection(
                String.valueOf(deviceId),
                point.getPointName(),
                projectCode,  // 使用项目编号
                new ProjectDataPushAddApiUtils.CollectionCallback() {
                    @Override
                    public void onSuccess(String response) {
                        runOnUiThread(() -> {
                            // 更新采集状态
                            point.setNitrogen("已采集");
                            point.setPhosphorus("已采集");
                            point.setPotassium("已采集");
                            point.setCollectionTime("2025-07-05"); // 使用实际采集时间
                            point.setStatus("已采集");

                            adapter.notifyItemChanged(position);
                            Toast.makeText(ProjectDataPushAdd.this,
                                    deviceName + " 采集完成",
                                    Toast.LENGTH_SHORT).show();
                        });
                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        runOnUiThread(() -> {
                            point.setStatus("采集失败");
                            adapter.notifyItemChanged(position);
                            Toast.makeText(ProjectDataPushAdd.this,
                                    point.getPointName() + " 采集失败: " + errorMessage,
                                    Toast.LENGTH_SHORT).show();
                            adapter.enableItemAtPosition(position);
                        });
                    }
                    @Override
                    public void onError(Throwable throwable) {
                        runOnUiThread(() -> {
                            point.setStatus("网络错误");
                            adapter.notifyItemChanged(position);
                            Toast.makeText(ProjectDataPushAdd.this,
                                    "网络错误: " + throwable.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            adapter.enableItemAtPosition(position);
                        });
                    }
                }
        );
    }

    /**
     * 验证并提交所有采集数据
     */
    private void validateAndSubmit() {
        // 检查所有采集点是否已完成
        for (int i = 0; i < collectionPoints.size(); i++) {
            if ("未采集".equals(collectionPoints.get(i).getCollectionTime())) {
                Toast.makeText(this, "第" + (i+1) + "个采集点未完成", Toast.LENGTH_SHORT).show();
                rvCollectionPoints.smoothScrollToPosition(i); // 滚动到未完成的项
                return; // 终止提交
            }
        }

        // 模拟数据提交过程
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Toast.makeText(this, projectCode + " 数据提交成功", Toast.LENGTH_SHORT).show();
            finish(); // 提交完成后关闭当前界面
        }, 1000); // 1000ms延迟模拟网络请求
    }
}
