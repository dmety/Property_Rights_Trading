package com.blockexplore.model;

import lombok.Data;
@Data
public class ContractInfo {
    private String projectCode; // 项目编号
    private String contractCode; // 合约编号
    private String successPrice; // 成交价格
    private String successPriceUnit; // 成交价格单位
    private String sumSuccessPrice; // 成交总价
    private String contractDate; // 合约成立日期
    private String contractRollOutMode; // 合约轮出方式
    private String contractRollOutStartDate; // 轮出开始日期
    private String contractRollOutEndDate; // 轮出结束日期
    private String contractRollOutArea; // 轮出面积
}
