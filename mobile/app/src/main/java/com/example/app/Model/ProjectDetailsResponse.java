package com.example.app.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ProjectDetailsResponse {
    private int code;
    private String message;
    private DataBean data;


    public static class DataBean {
        @SerializedName("baseInfo")
        private BaseInfo baseInfo;
        @SerializedName("upInfo")
        private UpInfo upInfo;
        @SerializedName("contractInfo")
        private ContractInfo contractInfo;
        @SerializedName("transferor")
        private Transferor transferor;
        @SerializedName("transferee")
        private List<Object> transferee;

        public BaseInfo getBaseInfo() {
            return baseInfo;
        }

        public UpInfo getUpInfo() {
            return upInfo;
        }

        public ContractInfo getContractInfo() {
            return contractInfo;
        }

        public Transferor getTransferor() {
            return transferor;
        }

        public List<Object> getTransferee() {
            return transferee;
        }
    }

    public static class BaseInfo {
        @SerializedName("projectCode")
        private String projectCode;
        @SerializedName("rightNo")
        private String rightNo;
        @SerializedName("transKind")
        private String transKind;
        @SerializedName("aptUserCode")
        private String aptUserCode;
        @SerializedName("aptOrganCode")
        private String aptOrganCode;
        @SerializedName("aptDate")
        private String aptDate;
        @SerializedName("projectName")
        private String projectName;
        @SerializedName("userName")
        private String userName;
        @SerializedName("orgName")
        private String orgName;
        @SerializedName("projectStartDate")
        private String projectStartDate;
        @SerializedName("projectEndDate")
        private String projectEndDate;
        @SerializedName("projectStatus")
        private String projectStatus;
        @SerializedName("successPulic")
        private String successPublic;

        // Getters and Setters
        public String getProjectCode() { return projectCode; }
        public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
        public String getRightNo() { return rightNo; }
        public void setRightNo(String rightNo) { this.rightNo = rightNo; }
        public String getTransKind() { return transKind; }
        public void setTransKind(String transKind) { this.transKind = transKind; }
        public String getAptUserCode() { return aptUserCode; }
        public void setAptUserCode(String aptUserCode) { this.aptUserCode = aptUserCode; }
        public String getAptOrganCode() { return aptOrganCode; }
        public void setAptOrganCode(String aptOrganCode) { this.aptOrganCode = aptOrganCode; }
        public String getAptDate() { return aptDate; }
        public void setAptDate(String aptDate) { this.aptDate = aptDate; }
        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public String getOrgName() { return orgName; }
        public void setOrgName(String orgName) { this.orgName = orgName; }
        public String getProjectStartDate() { return projectStartDate; }
        public void setProjectStartDate(String projectStartDate) { this.projectStartDate = projectStartDate; }
        public String getProjectEndDate() { return projectEndDate; }
        public void setProjectEndDate(String projectEndDate) { this.projectEndDate = projectEndDate; }
        public String getProjectStatus() { return projectStatus; }
        public void setProjectStatus(String projectStatus) { this.projectStatus = projectStatus; }
        public String getSuccessPublic() { return successPublic; }
        public void setSuccessPublic(String successPublic) { this.successPublic = successPublic; }
    }

    public static class UpInfo {
        @SerializedName("projectCode")
        private String projectCode;
        @SerializedName("upStatus")
        private String upStatus;
        @SerializedName("upStartDate")
        private String upStartDate;
        @SerializedName("upEndDate")
        private String upEndDate;
        @SerializedName("upPrice")
        private String upPrice;
        @SerializedName("upPriceUnit")
        private String upPriceUnit;
        @SerializedName("rollOutMode")
        private String rollOutMode;
        @SerializedName("rollOutArea")
        private String rollOutArea;
        @SerializedName("isPubUp")
        private String isPubUp;

        // Getters and Setters
        public String getProjectCode() { return projectCode; }
        public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
        public String getUpStatus() { return upStatus; }
        public void setUpStatus(String upStatus) { this.upStatus = upStatus; }
        public String getUpStartDate() { return upStartDate; }
        public void setUpStartDate(String upStartDate) { this.upStartDate = upStartDate; }
        public String getUpEndDate() { return upEndDate; }
        public void setUpEndDate(String upEndDate) { this.upEndDate = upEndDate; }
        public String getUpPrice() { return upPrice; }
        public void setUpPrice(String upPrice) { this.upPrice = upPrice; }
        public String getUpPriceUnit() { return upPriceUnit; }
        public void setUpPriceUnit(String upPriceUnit) { this.upPriceUnit = upPriceUnit; }
        public String getRollOutMode() { return rollOutMode; }
        public void setRollOutMode(String rollOutMode) { this.rollOutMode = rollOutMode; }
        public String getRollOutArea() { return rollOutArea; }
        public void setRollOutArea(String rollOutArea) { this.rollOutArea = rollOutArea; }
        public String getIsPubUp() { return isPubUp; }
        public void setIsPubUp(String isPubUp) { this.isPubUp = isPubUp; }
    }

    public static class ContractInfo {
        @SerializedName("projectCode")
        private String projectCode;
        @SerializedName("contractCode")
        private String contractCode;
        @SerializedName("successPrice")
        private String successPrice;
        @SerializedName("successPriceUnit")
        private String successPriceUnit;
        @SerializedName("sumSuccessPrice")
        private String sumSuccessPrice;
        @SerializedName("contractDate")
        private String contractDate;
        @SerializedName("contractRollOutMode")
        private String contractRollOutMode;
        @SerializedName("contractRollOutStartDate")
        private String contractRollOutStartDate;
        @SerializedName("contractRollOutEndDate")
        private String contractRollOutEndDate;
        @SerializedName("contractRollOutArea")
        private String contractRollOutArea;

        // Getters and Setters
        public String getProjectCode() { return projectCode; }
        public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
        public String getContractCode() { return contractCode; }
        public void setContractCode(String contractCode) { this.contractCode = contractCode; }
        public String getSuccessPrice() { return successPrice; }
        public void setSuccessPrice(String successPrice) { this.successPrice = successPrice; }
        public String getSuccessPriceUnit() { return successPriceUnit; }
        public void setSuccessPriceUnit(String successPriceUnit) { this.successPriceUnit = successPriceUnit; }
        public String getSumSuccessPrice() { return sumSuccessPrice; }
        public void setSumSuccessPrice(String sumSuccessPrice) { this.sumSuccessPrice = sumSuccessPrice; }
        public String getContractDate() { return contractDate; }
        public void setContractDate(String contractDate) { this.contractDate = contractDate; }
        public String getContractRollOutMode() { return contractRollOutMode; }
        public void setContractRollOutMode(String contractRollOutMode) { this.contractRollOutMode = contractRollOutMode; }
        public String getContractRollOutStartDate() { return contractRollOutStartDate; }
        public void setContractRollOutStartDate(String contractRollOutStartDate) { this.contractRollOutStartDate = contractRollOutStartDate; }
        public String getContractRollOutEndDate() { return contractRollOutEndDate; }
        public void setContractRollOutEndDate(String contractRollOutEndDate) { this.contractRollOutEndDate = contractRollOutEndDate; }
        public String getContractRollOutArea() { return contractRollOutArea; }
        public void setContractRollOutArea(String contractRollOutArea) { this.contractRollOutArea = contractRollOutArea; }
    }

    public static class Transferor {
        @SerializedName("userType")
        private String userType;
        @SerializedName("projectCode")
        private String projectCode;
        @SerializedName("transferorName")
        private String transferorName;
        @SerializedName("cardType")
        private String cardType;
        @SerializedName("transferorCardNo")
        private String transferorCardNo;
        @SerializedName("organRegNo")
        private String organRegNo;
        @SerializedName("telephone")
        private String telephone;
        @SerializedName("email")
        private String email;
        @SerializedName("condition")
        private String condition;
        @SerializedName("transMode")
        private String transMode;

        // Getters and Setters
        public String getUserType() { return userType; }
        public void setUserType(String userType) { this.userType = userType; }
        public String getProjectCode() { return projectCode; }
        public void setProjectCode(String projectCode) { this.projectCode = projectCode; }
        public String getTransferorName() { return transferorName; }
        public void setTransferorName(String transferorName) { this.transferorName = transferorName; }
        public String getCardType() { return cardType; }
        public void setCardType(String cardType) { this.cardType = cardType; }
        public String getTransferorCardNo() { return transferorCardNo; }
        public void setTransferorCardNo(String transferorCardNo) { this.transferorCardNo = transferorCardNo; }
        public String getOrganRegNo() { return organRegNo; }
        public void setOrganRegNo(String organRegNo) { this.organRegNo = organRegNo; }
        public String getTelephone() { return telephone; }
        public void setTelephone(String telephone) { this.telephone = telephone; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getCondition() { return condition; }
        public void setCondition(String condition) { this.condition = condition; }
        public String getTransMode() { return transMode; }
        public void setTransMode(String transMode) { this.transMode = transMode; }
    }

    // Root class getters and setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public DataBean getData() { return data; }
    public void setData(DataBean data) { this.data = data; }
}
