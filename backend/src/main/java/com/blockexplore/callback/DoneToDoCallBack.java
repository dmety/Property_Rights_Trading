package com.blockexplore.callback;

import lombok.Data;
//project_code
//        right_no
//        project_name
//        trans_kind
//        apt_user_id
//        apt_org_id
//        apt_date
//        project_status
//        sum_success_price
//        create_time
//        edit_time
//        contract_code
//        is_pub_up
//        is_pub_success

@Data
public class DoneToDoCallBack {
    private String projectCode;
    private String rightNo;
    private String projectName;
    private String transKind;
    private String aptUserId;
    private String aptOrganId;
    private String aptDate;
    private String projectStatus;
    private String sumSuccessPrice;
    private String createTime;
    private String editTime;
    private String contractCode;
    private String isPubUp;
    private String isPubSuccess;
}
