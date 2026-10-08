package com.blockexplore.model;

import lombok.Data;

@Data
public class DIDContract {
    private String did;
    private String context;
    private String version;
    private String createTime;
    private String publicKey;
}
