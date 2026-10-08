package com.blockexplore.model;

import lombok.Data;

@Data
public class ConTractUser {
    private String id;
    private String cardNo;
    private String userAddr;
    private String loginName;
    private String role;
    private String orgId;
    private Boolean status;
    private String orgName;
    private String email;
    private String phone;
}
