package com.example.app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app.Model.AssigneeMainItem;
import com.example.testforenv.R;

import java.util.List;

/**
 * RecyclerView适配器 - 处理项目列表项的显示和交互
 * 功能：
 * 1. 绑定数据到列表项视图
 * 2. 处理"详情"和"受让"按钮点击事件
 */
public class AssigneeMainAdapter extends RecyclerView.Adapter<AssigneeMainAdapter.ViewHolder> {

    /**
     * 操作类型枚举：区分详情按钮和受让按钮
     */
    public enum Action {
        DETAIL,    // 查看详情操作
        ASSIGNMENT // 受让填写操作
    }

    /**
     * 自定义点击事件接口
     */
    public interface OnItemClickListener {
        void onItemClick(int position, Action action);
    }

    private final List<AssigneeMainItem> items;
    private final OnItemClickListener listener;

    public AssigneeMainAdapter(List<AssigneeMainItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 加载列表项布局
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_assignee_main, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssigneeMainItem item = items.get(position);

        // 绑定数据到ViewHolder
        holder.projectCodeText.setText(item.getProjectCode());
        holder.projectNameText.setText(item.getProjectName());
        holder.rollOutModeText.setText(item.getRollOutMode());
        holder.upPriceText.setText(String.format("%s %s",
                item.getUpPrice(),
                item.getUpPriceUnit()));

        // 设置按钮点击事件
        holder.detailBtn.setOnClickListener(v ->
                listener.onItemClick(position, Action.DETAIL));

        holder.assignmentBtn.setOnClickListener(v ->
                listener.onItemClick(position, Action.ASSIGNMENT));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * ViewHolder内部类 - 缓存列表项视图控件
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView projectCodeText;
        final TextView projectNameText;
        final TextView rollOutModeText;
        final TextView upPriceText;
        final Button detailBtn;
        final Button assignmentBtn;

        public ViewHolder(View view) {
            super(view);
            // 初始化视图控件
            projectCodeText = view.findViewById(R.id.project_code);
            projectNameText = view.findViewById(R.id.project_name);
            rollOutModeText = view.findViewById(R.id.roll_out_mode);
            upPriceText = view.findViewById(R.id.up_price);
            detailBtn = view.findViewById(R.id.detail_btn);
            assignmentBtn = view.findViewById(R.id.assignment_btn);
        }
    }
}
