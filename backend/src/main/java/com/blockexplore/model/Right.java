package com.blockexplore.model;

import lombok.Data;
// 产权实体
@Data
public class Right {
    private String rightNo;
    private String organId;
    private String rightName;
    private String createTime;
    private String editTime;
    private String transKind;
    private String rightOwner;
    private String rightCertCode;
    private String rightCardId;
    private String blockNumber;
}
