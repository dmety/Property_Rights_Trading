package com.example.app.Frame;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testforenv.R;
import com.example.app.Adapter.AnnouncementAdapter;
import com.example.app.Config.EnvConfig;
import com.example.app.Model.ProjectBaseInfo;
import com.example.app.Utils.OkHttpUtil;
import com.example.app.components.Announcement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementFragment extends Fragment {

    private RecyclerView recyclerView;
    private AnnouncementAdapter adapter;
    private List<Announcement> announcementList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_announcement, container, false);

        recyclerView = view.findViewById(R.id.recycler_view_announcement);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化数据列表
        announcementList = new ArrayList<>();

        // 设置适配器，传递上下文和数据
        adapter = new AnnouncementAdapter(getContext(), announcementList);
        recyclerView.setAdapter(adapter);

        // 发送网络请求获取公告数据
        OkHttpUtil.sendGetRequest(EnvConfig.UP_CONTRACT_LIST_URL, new OkHttpUtil.OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) throws JSONException {
                JSONObject responseObject = new JSONObject(response);  // 将 response 解析为 JSONObject
                JSONArray dataArray = responseObject.getJSONArray("data");  // 提取 "data" 字段

                // 清空列表以确保不重复添加
                announcementList.clear();

                for (int i = 0; i < dataArray.length(); i++) {
                    Announcement announcement = new Announcement();
                    JSONObject jsonObject = dataArray.getJSONObject(i);  // 获取 data 数组中的每个 JSONObject
                    JSONObject baseInfo = jsonObject.getJSONObject("baseInfo");  // 获取 baseInfo 对象
                    JSONObject contractInfo = jsonObject.getJSONObject("contractInfo");      // 获取 contractInfo 对象

                    announcement.setProjectId(baseInfo.getString("projectCode"));
                    announcement.setProjectName(baseInfo.getString("projectName"));
                    announcement.setFlowWay(contractInfo.getString("contractRollOutMode"));
                    announcement.setSuccessAreas(contractInfo.getString("contractRollOutArea") + "亩");
                    announcement.setSuccessPrice(contractInfo.getString("successPrice") + "元");

                    // 将公告添加到列表
                    announcementList.add(announcement);
                }

                // 更新UI必须在主线程
                requireActivity().runOnUiThread(() -> {
                    // 检查 Fragment 是否仍然附加到 Activity
                    if (isAdded()) {
                        // 通知适配器数据已更改
                        adapter.notifyDataSetChanged();
                    }
                });

                return null;
            }


            @Override
            public void onFailure(Exception e) {
                // 处理失败情况
                e.printStackTrace(); // 可以添加适当的错误处理逻辑
            }

            @Override
            public void onError(String error) {
                // 处理错误情况
                System.err.println("Error: " + error); // 可以添加适当的错误处理逻辑
            }
        });

        return view;
    }
}
