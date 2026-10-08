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
import com.example.app.Activity.DealActivity;
import com.example.app.components.Announcement;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private List<Announcement> announcementList;
    private Context context;  // 添加上下文以便启动新活动

    public AnnouncementAdapter(Context context, List<Announcement> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);
        holder.projectId.setText(announcement.getProjectId());
        holder.projectName.setText(announcement.getProjectName());
        holder.successPrice.setText(announcement.getSuccessPrice());
        holder.successAreas.setText(announcement.getSuccessAreas());
        holder.flowWay.setText(announcement.getFlowWay());

        // 设置点击事件
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DealActivity.class);
            intent.putExtra("PROJECT_ID", announcement.getProjectId()); // 传递项目编号
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectId, projectName, successPrice, successAreas,flowWay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectId = itemView.findViewById(R.id.tv_project_id);
            projectName = itemView.findViewById(R.id.tv_project_name);
            successPrice = itemView.findViewById(R.id.tv_success_price);
//            auditPerson = itemView.findViewById(R.id.tv_audit_person)
            successAreas = itemView.findViewById(R.id.tv_success_areas);
            flowWay = itemView.findViewById(R.id.tv_flow_way);
        }
    }
}
