package com.example.app.Model;

import java.io.Serializable;

/**
 * 权证详情响应实体类
 * 包含权证相关的完整详情信息，采用嵌套结构设计
 * 实现Serializable接口以支持序列化传输
 */
public class AssigneeMainDetailsResponse implements Serializable {
    private int code;           // 响应状态码
    private String message;     // 响应消息
    private DataBean data;      // 核心数据对象

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public DataBean getData() { return data; }

    /**
     * 核心数据容器Bean
     * 包含权证的所有详情信息子模块
     */
    public static class DataBean implements Serializable {
        private RightCommonInfo rightCommonInfo;    // 权证基础信息
        private TransactionInfo transactionInfo;    // 交易过程信息
        private LandDetail landDetail;              // 土地详细信息
        private RightAddress rightAddress;          // 地理位置信息

        // region Getter方法
        public RightCommonInfo getRightCommonInfo() { return rightCommonInfo; }
        public TransactionInfo getTransactionInfo() { return transactionInfo; }
        public LandDetail getLandDetail() { return landDetail; }
        public RightAddress getRightAddress() { return rightAddress; }
        // endregion
    }

    /**
     * 权证基础信息实体
     * 映射权属证书的核心字段
     */
    public static class RightCommonInfo implements Serializable {
        private String rightNo;         // 权证编号
        private String rightName;       // 权证名称
        private String orgName;         // 发证机构
        private String rightOwner;      // 权属人名称
        private String ownerShip;       // 所有权类型
        private String userName;        // 受理登记人(JYSL)
        private String useStartDate;    // 使用起始日期
        private String useEndDate;      // 使用截止日期
        private String rightCertCode;   // 权证认证代码/其他编码

        // region Getter方法
        public String getRightNo() { return rightNo; }
        public String getRightName() { return rightName; }
        public String getOrgName() { return orgName; }
        public String getRightOwner() { return rightOwner; }
        public String getOwnerShip() { return ownerShip; }
        public String getUserName() { return userName; }
        public String getUseStartDate() { return useStartDate; }
        public String getUseEndDate() { return useEndDate; }
        public String getRightCertCode() { return rightCertCode; }
        // endregion
    }

    /**
     * 交易信息实体
     * 记录当前权证在交易系统中的状态信息
     */
    public static class TransactionInfo implements Serializable {
        private String projectName;     // 项目名称(如:XXXX100亩土地承包权出租)
        private String projectCode;     // 项目编码(如:XM2025A00007)
        private String transactionOrg;  // 交易机构(如:XX土地交易中心)
        private String acceptDate;      // 受理时间(格式:yyyy-MM-dd HH:mm:ss)
        private String projectStep;     // 当前项目阶段(如:受让受理)

        // region Getter方法
        public String getProjectName() { return projectName; }
        public String getProjectCode() { return projectCode; }
        public String getTransactionOrg() { return transactionOrg; }
        public String getAcceptDate() { return acceptDate; }
        public String getProjectStep() { return projectStep; }
        // endregion
    }

    /**
     * 土地详情实体
     * 包含土地相关的物理属性信息
     */
    public static class LandDetail implements Serializable {
        private String landArea;        // 土地面积(带单位)
        private String landNature;      // 土地性质(如:耕地/建设用地等)

        // region Getter方法
        public String getLandArea() { return landArea; }
        public String getLandNature() { return landNature; }
        // endregion
    }

    /**
     * 权证地址实体
     * 采用三级行政区划结构
     */
    public static class RightAddress implements Serializable {
        private String province;    // 省级行政区划
        private String city;        // 市级行政区划
        private String region;      // 区县级行政区划

        // region Getter方法
        public String getProvince() { return province; }
        public String getCity() { return city; }
        public String getRegion() { return region; }
        // endregion
    }
}
