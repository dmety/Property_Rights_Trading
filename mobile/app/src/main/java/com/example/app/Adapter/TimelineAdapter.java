package com.example.app.Adapter; // 请替换为您的包名

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.components.TimelineItem;
import com.example.app.components.TimelineItemView;

import java.util.List;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {

    private List<TimelineItem> timelineItems; // 创建一个模型类用来存储数据

    public TimelineAdapter(List<TimelineItem> timelineItems) {
        this.timelineItems = timelineItems;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TimelineViewHolder(new TimelineItemView(parent.getContext()));
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
        TimelineItem item = timelineItems.get(position);
        holder.timelineItemView.setTitle(item.getTitle());
        holder.timelineItemView.setContent(item.getContent());
        holder.timelineItemView.setTime(item.getTime());
    }

    @Override
    public int getItemCount() {
        return timelineItems.size();
    }

    static class TimelineViewHolder extends RecyclerView.ViewHolder {
        TimelineItemView timelineItemView;

        public TimelineViewHolder(@NonNull TimelineItemView itemView) {
            super(itemView);
            timelineItemView = itemView;
        }
    }
}
