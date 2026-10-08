package com.blockexplore.request;

import lombok.Data;

@Data
public class AppraisalCertInfoRequest {
    private String projectCode;
    private String appraisalCertNo;
    private String appraisalDate;
}
