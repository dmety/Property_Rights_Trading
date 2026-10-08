package com.blockexplore.model;

import lombok.Data;
@Data
public class ProjectBaseInfo {
    private String projectCode;
    private String rightNo;
    private String transKind;
    private String aptUserCode;
    private String aptOrganCode;
    private String aptDate;
    private String projectName;
    //
    private String userName;
    private String orgName;

    private String projectStartDate;
    private String projectEndDate;
    private String projectStatus;
    private String successPulic;
}
