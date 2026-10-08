package com.example.app.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app.Model.CollectionPoint;
import com.example.app.Model.Device;
import com.example.testforenv.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionPointAdapter extends RecyclerView.Adapter<CollectionPointAdapter.ViewHolder> {

    private List<CollectionPoint> collectionPoints;
    private RecyclerView recyclerView;
    private DeviceSelectionListener deviceSelectionListener;

    public interface DeviceSelectionListener {
        void onDevicesRequested(CollectionPoint point, int position);
        void onCollectionCommandSent(CollectionPoint point, String deviceName, int position);
    }

    public CollectionPointAdapter(List<CollectionPoint> collectionPoints, RecyclerView recyclerView) {
        this.collectionPoints = collectionPoints != null ? collectionPoints : new ArrayList<>();
        this.recyclerView = recyclerView;
    }

    public void setDeviceSelectionListener(DeviceSelectionListener listener) {
        this.deviceSelectionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collection_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CollectionPoint point = collectionPoints.get(position);

        holder.tvPointName.setText(point.getPointName());
        holder.tvNitrogen.setText(point.getNitrogen());
        holder.tvPhosphorus.setText(point.getPhosphorus());
        holder.tvPotassium.setText(point.getPotassium());
        holder.tvCollectTime.setText(point.getCollectionTime());

        // 初始化Spinner，只做Adapter和OnTouch，不设置监听
        initSpinner(holder, point, position);

        // 类型安全监听，只设置一次
        if (!holder.listenerBound) {
            holder.spinnerDevices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    Object selectedObj = parent.getItemAtPosition(pos);

                    String selectedName = null;
                    long deviceId = -1;

                    if (selectedObj instanceof Device) {
                        Device device = (Device) selectedObj;
                        selectedName = device.getName();
                        deviceId = device.getId();
                    } else if (selectedObj instanceof String) {
                        selectedName = (String) selectedObj;
                    }

                    if (deviceSelectionListener != null
                            && deviceId != -1
                            && !"选择设备".equals(selectedName)
                            && !"加载中...".equals(selectedName)
                            && !"无可用设备".equals(selectedName)) {
                        point.setSelectedDeviceId(deviceId);
                        deviceSelectionListener.onCollectionCommandSent(
                                point, selectedName, holder.getAdapterPosition()
                        );
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
            holder.listenerBound = true;
        }
    }

    // 只初始化Adapter和触发事件，不设置监听
    private void initSpinner(ViewHolder holder, CollectionPoint point, int position) {
        // 初始状态只显示"选择设备"
        ArrayAdapter<String> initAdapter = new ArrayAdapter<>(
                holder.itemView.getContext(),
                android.R.layout.simple_spinner_item,
                Collections.singletonList("选择设备"));
        initAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerDevices.setAdapter(initAdapter);

        // 设置触摸事件触发加载设备列表
        holder.spinnerDevices.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP && holder.spinnerDevices.getAdapter().getCount() <= 1) {
                fetchDevicesForPosition(holder.getAdapterPosition());
                return true; // 表示事件已消费
            }
            return false;
        });

        // 不再设置setOnItemSelectedListener
    }

    public void disableItemAtPosition(int position) {
        ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            holder.spinnerDevices.setEnabled(false);
        }
    }

    public void enableItemAtPosition(int position) {
        ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            holder.spinnerDevices.setEnabled(true);
        }
    }

    private void fetchDevicesForPosition(int position) {
        if (deviceSelectionListener != null && position != RecyclerView.NO_POSITION) {
            CollectionPoint point = collectionPoints.get(position);
            deviceSelectionListener.onDevicesRequested(point, position);

            // 更新UI显示加载状态
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                ArrayAdapter<String> loadingAdapter = new ArrayAdapter<>(
                        holder.itemView.getContext(),
                        android.R.layout.simple_spinner_item,
                        Collections.singletonList("加载中..."));
                loadingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                holder.spinnerDevices.setAdapter(loadingAdapter);
                holder.spinnerDevices.setEnabled(false);
            }
        }
    }

    public void updateDevicesForPosition(int position, List<Device> devices) {
        if (position >= 0 && position < collectionPoints.size()) {
            ViewHolder holder = (ViewHolder) recyclerView.findViewHolderForAdapterPosition(position);
            if (holder != null) {
                if (devices != null && !devices.isEmpty()) {
                    // 推荐加一个“请选择设备”作为第一个项
                    List<Device> showList = new ArrayList<>();
                    showList.add(new Device((long) -1, "请选择设备"));
                    showList.addAll(devices);

                    ArrayAdapter<Device> adapter = new ArrayAdapter<Device>(
                            holder.itemView.getContext(),
                            android.R.layout.simple_spinner_item,
                            showList
                    ) {
                        @Override
                        public View getView(int pos, View convertView, ViewGroup parent) {
                            View view = super.getView(pos, convertView, parent);
                            TextView textView = view.findViewById(android.R.id.text1);
                            Device device = getItem(pos);
                            textView.setText(device != null ? device.getName() : "");
                            return view;
                        }
                    };
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.spinnerDevices.setAdapter(adapter);
                    holder.spinnerDevices.setEnabled(true);
                } else {
                    ArrayAdapter<String> emptyAdapter = new ArrayAdapter<>(
                            holder.itemView.getContext(),
                            android.R.layout.simple_spinner_item,
                            new String[]{"无可用设备"});
                    emptyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    holder.spinnerDevices.setAdapter(emptyAdapter);
                    holder.spinnerDevices.setEnabled(false);
                    collectionPoints.get(position).setSelectedDeviceId(-1L);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return collectionPoints.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPointName;
        TextView tvNitrogen;
        TextView tvPhosphorus;
        TextView tvPotassium;
        TextView tvCollectTime;
        Spinner spinnerDevices;
        boolean listenerBound = false;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPointName = itemView.findViewById(R.id.tv_point_name);
            tvNitrogen = itemView.findViewById(R.id.tv_nitrogen);
            tvPhosphorus = itemView.findViewById(R.id.tv_phosphorus);
            tvPotassium = itemView.findViewById(R.id.tv_potassium);
            tvCollectTime = itemView.findViewById(R.id.tv_collect_time);
            spinnerDevices = itemView.findViewById(R.id.spinner_devices);
        }
    }
}
