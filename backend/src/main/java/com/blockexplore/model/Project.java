package com.blockexplore.model;

import lombok.Data;

@Data
public class Project {
    private String projectCode;
    private String rightNo;
    private String projectStatus;
    private String projectName;
    private String transKind;
    private String aptUserId;
    private String aptOrganId;
    private String aptDate;
    private String createTime;
    private String editTime;
    private String sumSuccessPrice;
    private String projectStartDate;
    private String projectEndDate;
    private String area;
    private String perimeter;
}
