package com.example.app.components;

public class Announcement {
    private String projectId;
    private String projectName;
    private String flowWay;
    private String successPrice;
    private String successAreas;

    public Announcement() {
    }

    public Announcement(String projectId, String projectName, String flowWay, String successPrice, String successAreas) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.flowWay = flowWay;
        this.successPrice = successPrice;
        this.successAreas = successAreas;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public String getSuccessPrice() {
        return successPrice;
    }

    public void setSuccessPrice(String successPrice) {
        this.successPrice = successPrice;
    }

    public String getSuccessAreas() {
        return successAreas;
    }

    public void setSuccessAreas(String successAreas) {
        this.successAreas = successAreas;
    }
}
