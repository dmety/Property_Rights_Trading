package com.example.app.Model;

/**
 * 受让方信息数据模型
 * 用于封装土地/项目受让方的所有相关信息
 * 对应服务器 API 接口中的受让方数据结构
 */
public class TransfereeModel {
    // 字段说明注释（中英文对照）
    private String id;                      // 主键ID / Primary key ID
    private String projectCode;             // 关联的项目编号 / Associated project code
    private String userAddr;                // 用户地址 / User address
    private String transfereeName;          // 受让方名称（个人或机构） / Transferee name (individual or organization)
    private String cardType;                // 证件类型（身份证/营业执照等） / ID type (ID card/business license, etc.)
    private String transfereeCardNo;        // 证件号码 / ID number
    private String organRegNo;              // 组织机构注册号 / Organization registration number
    private String telephone;               // 联系电话 / Contact phone number
    private String email;                   // 电子邮箱 / Email address
    private String condition;               // 受让条件 / Transfer conditions
    private String otherMatter;             // 其他事项 / Other matters
    private String legalRepresentative;     // 法定代表人（企业） / Legal representative (for enterprises)
    private String userType;                // 用户类型（自然人/法人/其他组织） / User type (Natural person/Legal person/Other organization)
    private String applyDate;               // 申请日期(yyyy-MM-dd格式) / Application date (format yyyy-MM-dd)
    private String registeredCapital;       // 注册资本（企业） / Registered capital (for enterprises)
    private String willPrice;               // 意向价格 / Intended price
    private String willPriceUnit;           // 价格单位（元/万元等） / Price unit (Yuan/Ten thousand Yuan, etc.)

    /**
     * 构造函数
     * @param projectCode 必须关联的项目编号 / Required associated project code
     */
    public TransfereeModel(String projectCode) {
        this.projectCode = projectCode;
    }

    /******************************************
     * Getter 和 Setter 方法
     * 自动生成的基本方法，每个字段都有对应的方法
     *
     * Auto-generated basic methods, each field has corresponding methods
     ******************************************/

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr;
    }

    public String getTransfereeName() {
        return transfereeName;
    }

    public void setTransfereeName(String transfereeName) {
        this.transfereeName = transfereeName;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getTransfereeCardNo() {
        return transfereeCardNo;
    }

    public void setTransfereeCardNo(String transfereeCardNo) {
        this.transfereeCardNo = transfereeCardNo;
    }

    public String getOrganRegNo() {
        return organRegNo;
    }

    public void setOrganRegNo(String organRegNo) {
        this.organRegNo = organRegNo;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getOtherMatter() {
        return otherMatter;
    }

    public void setOtherMatter(String otherMatter) {
        this.otherMatter = otherMatter;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getRegisteredCapital() {
        return registeredCapital;
    }

    public void setRegisteredCapital(String registeredCapital) {
        this.registeredCapital = registeredCapital;
    }

    public String getWillPrice() {
        return willPrice;
    }

    public void setWillPrice(String willPrice) {
        this.willPrice = willPrice;
    }

    public String getWillPriceUnit() {
        return willPriceUnit;
    }

    public void setWillPriceUnit(String willPriceUnit) {
        this.willPriceUnit = willPriceUnit;
    }
}
