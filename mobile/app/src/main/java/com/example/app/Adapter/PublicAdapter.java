package com.example.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Activity.Me.Info.AssigneeMainDetails;
import com.example.app.Activity.Me.Info.AssigneeMainTransferee;
import com.example.testforenv.R;
import com.example.app.Activity.DealActivity;
import com.example.app.Model.PublicBaseInfo;

import java.util.List;

public class PublicAdapter extends RecyclerView.Adapter<PublicAdapter.ViewHolder> {

    private List<PublicBaseInfo> publicList;
    private Context context;

    public PublicAdapter(Context context, List<PublicBaseInfo> publicList) {
        this.context = context;
        this.publicList = publicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_publicfragment, parent, false); // 改为新布局文件
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PublicBaseInfo item = publicList.get(position);

        // 绑定数据到新版UI组件
        holder.projectCode.setText(item.getProjectCode());
        holder.projectName.setText(item.getProjectName());
        holder.transKind.setText(item.getTransKind());
        holder.rollOutMode.setText(item.getFlowWay());
        holder.upStartDate.setText(item.getUpTime());
        holder.rollOutArea.setText(item.getFlowAreas());
        holder.upPrice.setText(item.getUpPrice());

        // 处理"成为受理人"按钮状态
        boolean isAcceptable = "受让受理".equals(item.getProjectStatus());
        holder.becomeAcceptorBtn.setEnabled(isAcceptable);
        holder.becomeAcceptorBtn.setAlpha(isAcceptable ? 1.0f : 0.5f); // 禁用时半透明

        // 按钮点击事件
        holder.becomeAcceptorBtn.setOnClickListener(v -> {
            if (isAcceptable) {
                Intent intent = new Intent(context, AssigneeMainTransferee.class);
                intent.putExtra("projectCode", item.getProjectCode());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return publicList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // 新组件对应的UI元素
        TextView projectCode, projectName, transKind, rollOutMode,
                upStartDate, rollOutArea, upPrice;
        Button becomeAcceptorBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectCode = itemView.findViewById(R.id.projectCode);
            projectName = itemView.findViewById(R.id.projectName);
            transKind = itemView.findViewById(R.id.transKind);
            rollOutMode = itemView.findViewById(R.id.flowWay);
            upStartDate = itemView.findViewById(R.id.upTime);
            rollOutArea = itemView.findViewById(R.id.rollOutAreas);
            upPrice = itemView.findViewById(R.id.upPrice);
            becomeAcceptorBtn = itemView.findViewById(R.id.become_acceptor_btn);
        }
    }

    // 可选：数据更新方法
    public void updateList(List<PublicBaseInfo> newList) {
        publicList = newList;
        notifyDataSetChanged();
    }
}
