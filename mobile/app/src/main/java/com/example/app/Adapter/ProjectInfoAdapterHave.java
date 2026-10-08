package com.example.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Activity.Me.Info.AssigneeMainDetails;
import com.example.app.Model.ProjectBaseInfo;
import com.example.testforenv.R;

import java.util.List;

public class ProjectInfoAdapterHave extends RecyclerView.Adapter<ProjectInfoAdapterHave.ViewHolder> {
    private Context context;
    private List<ProjectBaseInfo> projects;
    public ProjectInfoAdapterHave(Context context, List<ProjectBaseInfo> projects) {
        this.context = context;
        this.projects = projects;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 使用新的卡片布局
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_project_have, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectBaseInfo project = projects.get(position);

        // 绑定数据到新布局组件
        holder.projectCode.setText(project.getProjectCode());
        holder.projectName.setText(project.getProjectName());
        holder.transType.setText(project.getTransType());
        holder.projectStatus.setText(project.getProjectStatus());
        holder.doneTime.setText(project.getDoneTime());

        holder.detailBtn.setOnClickListener(v -> {
            // 创建跳转到AssigneeMainDetails的Intent
            Intent intent = new Intent(context, AssigneeMainDetails.class);

            // 注意这里使用项目编号(projectCode)作为RIGHT_NO参数传递
            // 确保project.getProjectCode()返回的值与AssigneeMainDetails需要的RIGHT_NO格式一致
            intent.putExtra("RIGHT_NO", project.getRightNo());

            context.startActivity(intent);
        });
    }
    @Override
    public int getItemCount() {
        return projects.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectCode; // 项目编号
        TextView projectName; // 项目名称
        TextView transType; // 流转方式
        TextView projectStatus;// 组织名称
        TextView doneTime; // 完成时间
        Button detailBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // 初始化视图组件
            projectCode = itemView.findViewById(R.id.projectCode);
            projectName = itemView.findViewById(R.id.projectName);
            transType = itemView.findViewById(R.id.transType);
            projectStatus = itemView.findViewById(R.id.projectStatus);
            doneTime = itemView.findViewById(R.id.doneTime);
            detailBtn = itemView.findViewById(R.id.detail_btn);
        }
    }
}
