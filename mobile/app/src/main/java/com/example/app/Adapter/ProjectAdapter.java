package com.example.app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.components.ProjectItem;
import com.example.testforenv.R;

import java.util.List;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {
    private List<ProjectItem> projectList;

    public ProjectAdapter(List<ProjectItem> projectList) {
        this.projectList = projectList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProjectItem item = projectList.get(position);
        holder.projectNameTextView.setText(item.getProjectName());
        holder.projectDescriptionTextView.setText(item.getProjectDescription());
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectNameTextView;
        TextView projectDescriptionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            projectNameTextView = itemView.findViewById(R.id.project_name);
            projectDescriptionTextView = itemView.findViewById(R.id.project_description);
        }
    }
}
