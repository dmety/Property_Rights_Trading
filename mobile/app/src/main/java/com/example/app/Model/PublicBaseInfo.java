package com.example.app.Model;

public class PublicBaseInfo {
    private String projectCode; // 项目编号
    private String projectName; // 项目名称
    private String flowWay; //  流转方式
    private String upTime; // 挂牌时间
    private String flowAreas; // 流转面积
    private String upPrice; // 挂牌价格

    private String projectStatus; // 项目状态

    // 新增字段
    private String transKind;    // 交易种类

    public PublicBaseInfo() {
    }

    public PublicBaseInfo(String projectCode, String projectName, String flowWay,
                          String upTime, String flowAreas, String upPrice,
                          String transKind, String projectStatus) {
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.flowWay = flowWay;
        this.upTime = upTime;
        this.flowAreas = flowAreas;
        this.upPrice = upPrice;
        this.transKind = transKind;
        this.projectStatus = projectStatus;
    }

    // region Getter & Setter
    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFlowWay() {
        return flowWay;
    }

    public void setFlowWay(String flowWay) {
        this.flowWay = flowWay;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getFlowAreas() {
        return flowAreas;
    }

    public void setFlowAreas(String flowAreas) {
        this.flowAreas = flowAreas;
    }

    public String getUpPrice() {
        return upPrice;
    }

    public void setUpPrice(String upPrice) {
        this.upPrice = upPrice;
    }

    public String getTransKind() {
        return transKind;
    }

    public void setTransKind(String transKind) {
        this.transKind = transKind;
    }

    public String getProjectStatus() {
        return projectStatus;
    }
    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

}
