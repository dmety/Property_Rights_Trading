package com.blockexplore.model;

import lombok.Data;
@Data
public class UpInfo {
    private String projectCode;
    private String upStatus;
    private String upStartDate;
    private String upEndDate;
    private String upPrice;
    private String upPriceUnit;
    private String rollOutMode;
    private String rollOutArea;
    private String isPubUp;
    private String userId;
}
