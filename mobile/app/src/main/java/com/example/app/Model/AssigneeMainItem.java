package com.example.app.Model;

/**
 * 数据模型类 - 表示单个受让项目项
 * 包含：
 * 1. 项目基本信息（编号、名称）
 * 2. 流转信息（方式、价格）
 * 3. 权证编号（用于详情查询）
 */
public class AssigneeMainItem {
    private final String projectCode;    // 项目编号
    private final String rightNo;       // 权证编号（新增字段）
    private final String projectName;    // 项目名称
    private final String rollOutMode;    // 流转方式
    private final String upPrice;        // 挂牌价格
    private final String upPriceUnit;    // 价格单位（如：元）

    public AssigneeMainItem(String projectCode, String rightNo, String projectName,
                            String rollOutMode, String upPrice, String upPriceUnit) {
        this.projectCode = projectCode;
        this.rightNo = rightNo;
        this.projectName = projectName;
        this.rollOutMode = rollOutMode;
        this.upPrice = upPrice;
        this.upPriceUnit = upPriceUnit;
    }

    // Getter方法
    public String getProjectCode() { return projectCode; }
    public String getRightNo() { return rightNo; } // 新增Getter
    public String getProjectName() { return projectName; }
    public String getRollOutMode() { return rollOutMode; }
    public String getUpPrice() { return upPrice; }
    public String getUpPriceUnit() { return upPriceUnit; }
}
