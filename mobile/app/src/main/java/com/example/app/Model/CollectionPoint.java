package com.example.app.Model;

public class CollectionPoint {
    private String pointName; // 采集点名称
    private String nitrogen; // 氮含量
    private String phosphorus; // 磷含量
    private String potassium; // 钾含量
    private String collectionTime; // 采集时间
    private long selectedDeviceId = -1; // 当前选中的设备ID
    private String status; // 采集状态（新增）

    public CollectionPoint(String pointName, String nitrogen, String phosphorus,
                           String potassium, String collectionTime) {
        this.pointName = pointName;
        this.nitrogen = nitrogen;
        this.phosphorus = phosphorus;
        this.potassium = potassium;
        this.collectionTime = collectionTime;
        this.status = collectionTime == null ? "未采集" : "已采集"; // 初始化状态
    }

    // Getters and Setters
    public String getPointName() { return pointName; }
    public void setPointName(String pointName) { this.pointName = pointName; }
    public String getNitrogen() { return nitrogen; }
    public void setNitrogen(String nitrogen) { this.nitrogen = nitrogen; }
    public String getPhosphorus() { return phosphorus; }
    public void setPhosphorus(String phosphorus) { this.phosphorus = phosphorus; }
    public String getPotassium() { return potassium; }
    public void setPotassium(String potassium) { this.potassium = potassium; }
    public String getCollectionTime() { return collectionTime; }
    public void setCollectionTime(String collectionTime) {
        this.collectionTime = collectionTime;
        this.status = "已采集"; // 设置时间的同时更新状态
    }
    public long getSelectedDeviceId() { return selectedDeviceId; }
    public void setSelectedDeviceId(long selectedDeviceId) {
        this.selectedDeviceId = selectedDeviceId;
    }

    // 新增状态方法
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
