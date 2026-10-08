package com.blockexplore.request;

import lombok.Data;
@Data
public class updateProjectRequest {
    private String projectCode;
    private String rightNo;
    private String transKind;
    private Long userId;
    private Long organId;
    private String aptDate;

}
