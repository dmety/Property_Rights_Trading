package com.blockexplore.request;
import lombok.Data;
@Data
public class updateUpInfoRequest {
    private String projectCode;
    private String upStatus;
    private String upStartDate;
    private String upEndDate;
    private Long upPrice;
    private Long upPriceUnit;
    private String rollOutMode;
    private Long rollOutArea;

}
