package com.example.app.Frame;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testforenv.R;
import com.example.app.Adapter.ProjectInfoAdapterHave;
import com.example.app.Model.ProjectBaseInfo;
import com.example.app.Utils.OkHttpUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProjectInfoFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProjectInfoAdapterHave adapter;
    private List<ProjectBaseInfo> projectInfolist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // 1. 加载布局文件
        View view = inflater.inflate(R.layout.fragment_project_info, container, false);

        // 2. 初始化RecyclerView
        initRecyclerView(view);

        // 3. 加载数据
        try {
            loadData();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return view;
    }

    /**
     * 初始化RecyclerView及其适配器
     */
    private void initRecyclerView(View rootView) {
        recyclerView = rootView.findViewById(R.id.recycler_view_project_info);
        // 设置布局管理器为线性布局
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // 初始化数据列表
        projectInfolist = new ArrayList<>();

        // 创建适配器并绑定到RecyclerView
        adapter = new ProjectInfoAdapterHave(requireContext(), projectInfolist);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 从网络加载数据
     */
    private void loadData() throws JSONException {
        // 发起网络请求获取项目信息

        // 从SharedPreferences中获取存储信息
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        // 获取 UserInfo
        JSONObject userInfo = new JSONObject(sharedPreferences.getString("userInfo", "{}"));
        // 获取 idNumber
        String idNumber = userInfo.optString("idNumber");
        OkHttpUtil.getAllProjectBaseInfoByIdNumber(idNumber, new OkHttpUtil.ProjectInfoCallback() {
            @Override
            public void onSuccess(List<ProjectBaseInfo> projectList) {
                // 检查Fragment是否仍然附加到Activity
                if (!isAdded() || getActivity() == null) {
                    Log.w("ProjectInfo", "Fragment not attached, ignoring callback");
                    return;
                }

                // 在主线程更新UI
                getActivity().runOnUiThread(() -> {
                    // 更新数据源
                    projectInfolist.clear();
                    projectInfolist.addAll(projectList);

                    // 通知适配器数据变化
                    adapter.notifyDataSetChanged();

                    // 可选：如果列表为空显示提示
                    if (projectList.isEmpty()) {
                        Toast.makeText(getContext(), "暂无项目数据", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.e("ProjectInfo", "请求失败: " + e.getMessage());
                        Toast.makeText(getContext(), "数据加载失败", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        Log.e("ProjectInfo", "请求错误: " + error);
                        Toast.makeText(getContext(), "发生错误: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 清理资源 (可选)
        recyclerView.setAdapter(null);
    }
}
