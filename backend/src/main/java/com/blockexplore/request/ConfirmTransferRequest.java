package com.blockexplore.request;

import lombok.Data;

@Data
public class ConfirmTransferRequest {
    private String projectCode;
    private String transfereeCardNo;
    private String userId;
}
