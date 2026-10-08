package com.blockexplore.request;

import lombok.Data;

@Data
public class QRCodeLoginRequest {
    // 传递者
    String vc;
    String status;
    // 接收者
    String query;
}
