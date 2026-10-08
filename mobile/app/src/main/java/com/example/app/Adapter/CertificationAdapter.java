package com.example.app.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.components.CertificationItem;
import com.example.testforenv.R;

import java.util.List;

public class CertificationAdapter extends RecyclerView.Adapter<CertificationAdapter.ViewHolder> {
    private List<CertificationItem> certificationList;

    public CertificationAdapter(List<CertificationItem> certificationList) {
        this.certificationList = certificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_certification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CertificationItem item = certificationList.get(position);
        holder.titleTextView.setText(item.getTitle());
        holder.descriptionTextView.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return certificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.certification_title);
            descriptionTextView = itemView.findViewById(R.id.certification_description);
        }
    }
}
