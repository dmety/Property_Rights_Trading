package com.example.app.Model;

import java.io.Serializable;
import java.util.List;

public class AssigneeMainResponse implements Serializable {
    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() { return code; }
    public String getMessage() { return message; }
    public List<DataBean> getData() { return data; }

    public static class DataBean implements Serializable {
        private BaseInfoBean baseInfo;
        private UpInfoBean upInfo;

        public BaseInfoBean getBaseInfo() { return baseInfo; }
        public UpInfoBean getUpInfo() { return upInfo; }
    }

    public static class BaseInfoBean implements Serializable {
        private String projectCode;
        private String projectName;
        private String rightNo;
        // Getters
        public String getProjectCode() { return projectCode; }
        public String getProjectName() { return projectName; }
        public String getRightNo() { return rightNo; }
    }

    public static class UpInfoBean implements Serializable {
        private String rollOutMode;
        private String upPrice;
        private String upPriceUnit;

        public String getRollOutMode() { return rollOutMode; }
        public String getUpPrice() { return upPrice; }
        public String getUpPriceUnit() { return upPriceUnit; }
    }

}
