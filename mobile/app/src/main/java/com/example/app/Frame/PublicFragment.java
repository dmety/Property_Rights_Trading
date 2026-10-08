package com.example.app.Frame;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testforenv.R;
import com.example.app.Adapter.PublicAdapter;
import com.example.app.Config.EnvConfig;
import com.example.app.Model.ProjectBaseInfo;
import com.example.app.Model.PublicBaseInfo;
import com.example.app.Utils.OkHttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PublicFragment extends Fragment {

    private RecyclerView recyclerView;
    private PublicAdapter adapter;
    private List<PublicBaseInfo> publicList;  // PublicBaseInfo 列表

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public, container, false);

        // 初始化 RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view_public);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化适配器和列表
        publicList = new ArrayList<>();
        adapter = new PublicAdapter(getContext(), publicList);
        recyclerView.setAdapter(adapter);

        // 发送 GET 请求获取数据
        OkHttpUtil.sendGetRequest(EnvConfig.IS_PUB_UP_URL, new OkHttpUtil.OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) throws JSONException {
                JSONObject responseObject = new JSONObject(response);  // 将 response 解析为 JSONObject
                JSONArray dataArray = responseObject.getJSONArray("data");  // 提取 "data" 字段

                publicList.clear();  // 清空列表
                Log.d("PublicFragment", "PublicList: " + dataArray);
                // 解析每个数据对象并添加到列表中
                for (int i = 0; i < dataArray.length(); i++) {
                    PublicBaseInfo publicBaseInfo = new PublicBaseInfo();
                    JSONObject jsonObject = dataArray.getJSONObject(i);  // 获取 data 数组中的每个 JSONObject
                    JSONObject baseInfo = jsonObject.getJSONObject("baseInfo");  // 获取 baseInfo 对象
                    JSONObject upInfo = jsonObject.getJSONObject("upInfo");      // 获取 upInfo 对象

                    publicBaseInfo.setProjectCode(baseInfo.getString("projectCode")); // 设置项目编号
                    publicBaseInfo.setProjectName(baseInfo.getString("projectName")); // 设置项目名称
                    publicBaseInfo.setTransKind(baseInfo.getString("transKind").equals("A") ? "土地":"其他种类"); // 设置交易种类
                    publicBaseInfo.setFlowAreas(upInfo.getString("rollOutArea") + "亩"); // 设置流转面积
                    publicBaseInfo.setFlowWay(upInfo.getString("rollOutMode")); // 获取流转方式
                    publicBaseInfo.setUpTime(upInfo.getString("upStartDate")); // 获取挂牌时间
                    publicBaseInfo.setUpPrice(upInfo.getString("upPrice") + "元"); // 设置挂牌单价

                    publicBaseInfo.setProjectStatus(baseInfo.getString("projectStatus")); // 设置项目状态

                    publicList.add(publicBaseInfo);  // 将解析好的对象添加到列表中
                }

                // 通知适配器数据已更新，确保在主线程上执行
                getActivity().runOnUiThread(() -> {
                    adapter.notifyDataSetChanged();
                });

                return null;
            }

            @Override
            public void onFailure(Exception e) {
                // 处理失败情况
                e.printStackTrace();
            }

            @Override
            public void onError(String error) {
                // 处理错误情况
                System.err.println("Error: " + error);
            }
        });

        return view;
    }
}
