package com.example.app.Model;
public class ProjectBaseInfo {
    private String projectCode;
    private String projectName;
    private String transType;
    private String projectStatus;
    private String orgName;
    private String doneTime;

    private String rightNo;

    public ProjectBaseInfo() {
    }

    public ProjectBaseInfo(String projectCode, String projectName, String transType, String projectStatus, String orgName, String doneTime) {
        this.projectCode = projectCode;
        this.projectName = projectName;
        this.transType = transType;
        this.projectStatus = projectStatus;
        this.orgName = orgName;
        this.doneTime = doneTime;
    }

    public ProjectBaseInfo(String projectCode,String rightNo, String projectName, String transType, String projectStatus, String orgName, String doneTime) {
        this.projectCode = projectCode;
        this.rightNo = rightNo;
        this.projectName = projectName;
        this.transType = transType;
        this.projectStatus = projectStatus;
        this.orgName = orgName;
        this.doneTime = doneTime;
    }

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

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDoneTime() {
        return doneTime;
    }

    public void setDoneTime(String doneTime) {
        this.doneTime = doneTime;
    }


    public String getRightNo() {
        return rightNo;
    }
}
