package com.blockexplore.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String loginName;
    private String loginPass;
    private String role; // 普通用户，监管机构，交易中心，确权机构
}
