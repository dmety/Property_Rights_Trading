package com.blockexplore.model;

import lombok.Data;
//project_code
//        user_id
//        org_id
//        project_status
//        reject_flag
//        remark
//        done_time

@Data
public class History {
    private String projectCode;
    private String userId;
    private String orgId;
    private String projectStatus;
    private Integer rejectFlag;
    private String remark;
    private String doneTime;
}
