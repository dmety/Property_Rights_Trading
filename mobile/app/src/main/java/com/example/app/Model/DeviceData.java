package com.example.app.Model;

public class DeviceData {
    private Long id;
    private Long deviceId;
    private String taskId;
    private String collectTime;
    private String dataName;
    private String dataValue;
    private String projectCode;
    private String acquisitionPoint;

    public DeviceData(Long id, Long deviceId, String taskId, String collectTime, String dataName, String dataValue, String projectCode, String acquisitionPoint) {
        this.id = id;
        this.deviceId = deviceId;
        this.taskId = taskId;
        this.collectTime = collectTime;
        this.dataName = dataName;
        this.dataValue = dataValue;
        this.projectCode = projectCode;
        this.acquisitionPoint = acquisitionPoint;
    }

    public DeviceData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getAcquisitionPoint() {
        return acquisitionPoint;
    }

    public void setAcquisitionPoint(String acquisitionPoint) {
        this.acquisitionPoint = acquisitionPoint;
    }
}
