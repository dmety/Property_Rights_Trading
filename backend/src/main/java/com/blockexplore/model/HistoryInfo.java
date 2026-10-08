package com.blockexplore.model;

import lombok.Data;

@Data
public class HistoryInfo {
    private String step;
    private String doneTime;
    private String doneOrgId;
    private String doneUserId;
    private String projectCode;
    private String rejectFlag;
    //
    private String userName;
    private String orgName;
}
