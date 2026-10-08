package com.example.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testforenv.R;
import com.example.app.Activity.ProjectInfoActivity;
import com.example.app.Model.ProjectBaseInfo;

import java.util.List;

public class ProjectInfoAdapter extends RecyclerView.Adapter<ProjectInfoAdapter.ViewHolder> {

    private List<ProjectBaseInfo> projectInfolist;
    private Context context;  // 上下文，用于启动新活动

    public ProjectInfoAdapter(Context context, List<ProjectBaseInfo> projectInfolist) {
        this.context = context;
        this.projectInfolist = projectInfolist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectBaseInfo project = projectInfolist.get(position);
        holder.projectId.setText(project.getProjectCode()); // 项目编号
        holder.projectName.setText(project.getProjectName()); // 项目名称
        holder.projectStatus.setText(project.getProjectStatus()); // 项目状态
        holder.transType.setText(project.getTransType()); // 流转方式
        holder.orgName.setText(project.getOrgName()); // 受理机构
        holder.doneTime.setText(project.getDoneTime()); // 受理时间

        // 点击卡片跳转到项目详情
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ProjectInfoActivity.class);
            intent.putExtra("PROJECT_ID", project.getProjectCode()); // 传递项目编号
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return projectInfolist.size();
    }

    // ViewHolder 内部类
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectId, projectName, projectStatus, transType, orgName, doneTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectId = itemView.findViewById(R.id.tv_project_id); // 项目编号
            projectName = itemView.findViewById(R.id.tv_project_name); // 项目名称
            projectStatus = itemView.findViewById(R.id.tv_project_status); // 项目状态
            transType = itemView.findViewById(R.id.transType); // 流转方式
            orgName = itemView.findViewById(R.id.orgName); // 受理机构
            doneTime = itemView.findViewById(R.id.doneTime); // 受理时间
        }
    }
}
