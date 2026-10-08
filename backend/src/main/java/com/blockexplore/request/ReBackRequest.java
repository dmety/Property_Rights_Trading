package com.blockexplore.request;

import lombok.Data;

@Data
public class ReBackRequest {
    private String projectCode;
    private Integer step;
    private Integer nowStep;
    private Integer userId;
    private String projectStatus;
}
