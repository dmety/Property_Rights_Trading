package com.blockexplore.callback;

import lombok.Data;

@Data
public class QRCodeLoginCallBack {
    String loginName;
    String userId;
    String orgId;
    String address;
    String role;
}
