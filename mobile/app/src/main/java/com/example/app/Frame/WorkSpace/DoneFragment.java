package com.example.app.Frame.WorkSpace;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.app.Config.EnvConfig;
import com.example.app.Model.ProjectBaseInfo;
import com.example.app.Utils.OkHttpUtil;
import com.example.testforenv.R;
import com.example.app.Activity.WorkSpace.DoneActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DoneFragment extends Fragment {

    private LinearLayout cardContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_done, container, false);

        // 获取 cardContainer
        cardContainer = rootView.findViewById(R.id.cardContainer);

        // 发起网络请求获取数据
        OkHttpUtil.sendGetRequest(EnvConfig.REVIEW_DONE_LIST_URL + "?userId=1", new OkHttpUtil.OkHttpCallback() {
            @Override
            public List<ProjectBaseInfo> onSuccess(String response) throws JSONException {
                JSONObject responseObject = new JSONObject(response);  // 将响应解析为 JSONObject
                JSONArray dataArray = responseObject.getJSONArray("data");  // 将响应解析为 JSONArray

                // 确保 UI 操作在主线程中进行
                getActivity().runOnUiThread(() -> {
                    // 清空容器，以防止重复添加卡片
                    cardContainer.removeAllViews();

                    // 循环遍历每个项目数据
                    for (int i = 0; i < dataArray.length(); i++) {
                        try {
                            JSONObject dataObject = dataArray.getJSONObject(i);
                            Log.d("DoneFragment", dataObject.toString());

                            // 获取项目数据
                            String projectNumber = dataObject.getString("projectCode");
                            String rejectFlag = dataObject.getString("rejectFlag");
                            String projectStatus = dataObject.getString("projectStatus");
                            String doneTime = dataObject.getString("doneTime");

                            // 调用方法动态添加卡片
                            addCardItem(projectNumber , projectStatus, rejectFlag, doneTime);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return null;
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }

            @Override
            public void onError(String error) {
                // 处理错误
            }
        });

        return rootView;
    }

    /**
     * 动态添加卡片项目
     *
     * @param projectNumber 项目编号
     * @param projectName   项目名称
     * @param projectStatus 项目状态
     * @param projectAuditor 审核人
     */
    private void addCardItem(String projectNumber, String projectName, String projectStatus, String projectAuditor) {
        // 使用 LayoutInflater 从 card_todone.xml 创建 View
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View cardView = inflater.inflate(R.layout.card_done, cardContainer, false);

        // 设置项目编号、项目名称等数据
        TextView projectNumberText = cardView.findViewById(R.id.project_number);
        TextView projectNameText = cardView.findViewById(R.id.project_name);
        TextView projectStatusText = cardView.findViewById(R.id.project_status);
        TextView projectAuditorText = cardView.findViewById(R.id.project_auditor);
        ImageView iconUp = cardView.findViewById(R.id.icon_up);
//        ImageView iconDown = cardView.findViewById(R.id.icon_down);
//        iconDown.setVisibility(View.GONE);
        projectNumberText.setText(projectNumber);
        projectNameText.setText(projectName);
        projectStatusText.setText(projectStatus);
        projectAuditorText.setText(projectAuditor);

        // 设置图标点击事件
        iconUp.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), DoneActivity.class);
            startActivity(intent);
        });

        // 将新建的卡片添加到 cardContainer
        cardContainer.addView(cardView);
    }
}
