package com.blockexplore.model.android;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
//project_code,project_name,trans_kind,apt_org_id,create_time
@Data
public class ProjectInfo {
    private String projectCode;
    private String rightNo;
    private String projectName;
    private String transKind;
    private String projectStatus;
    private String aptOrgId;
    private String createTime;
}
