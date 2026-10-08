package com.example.app.Model;

public class Device {

    private Long id;

    private String name;

    private String type;

    private String nodeId;

    private String devAddress;

    private String funcCode;

    private Long regBytes;

    private String regAddress;

    private Long dataBytes;

    private String dataLen;

    private String dataStart;

    private String dataStop;

    private String crcMode;

    private String collectCmd;

    private String startCmd;

    private String stopCmd;
    private String threshold;
    private String dataName;
    private String dataRange;
    private String alarm;
    private String alarmStatus;
    private String status;
    private String createTime;
    private String dataValue;
    private String projectName;
    private int userId;
    private String user;
    private String projectCode;
    private String acquisitionPpoint;

    public Device(Long id, String name, String type, String nodeId, String devAddress, String funcCode, Long regBytes, String regAddress, Long dataBytes, String dataLen, String dataStart, String dataStop, String crcMode, String collectCmd, String startCmd, String stopCmd, String threshold, String dataName, String dataRange, String alarm, String alarmStatus, String status, String createTime, String dataValue, String projectName, int userId, String user, String projectCode, String acquisitionPpoint) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.nodeId = nodeId;
        this.devAddress = devAddress;
        this.funcCode = funcCode;
        this.regBytes = regBytes;
        this.regAddress = regAddress;
        this.dataBytes = dataBytes;
        this.dataLen = dataLen;
        this.dataStart = dataStart;
        this.dataStop = dataStop;
        this.crcMode = crcMode;
        this.collectCmd = collectCmd;
        this.startCmd = startCmd;
        this.stopCmd = stopCmd;
        this.threshold = threshold;
        this.dataName = dataName;
        this.dataRange = dataRange;
        this.alarm = alarm;
        this.alarmStatus = alarmStatus;
        this.status = status;
        this.createTime = createTime;
        this.dataValue = dataValue;
        this.projectName = projectName;
        this.userId = userId;
        this.user = user;
        this.projectCode = projectCode;
        this.acquisitionPpoint = acquisitionPpoint;
    }

    public Device(Long id,String name) {
        this.id = id;
        this.name = name;
    }

    public Device() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getDevAddress() {
        return devAddress;
    }

    public void setDevAddress(String devAddress) {
        this.devAddress = devAddress;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public Long getRegBytes() {
        return regBytes;
    }

    public void setRegBytes(Long regBytes) {
        this.regBytes = regBytes;
    }

    public String getRegAddress() {
        return regAddress;
    }

    public void setRegAddress(String regAddress) {
        this.regAddress = regAddress;
    }

    public Long getDataBytes() {
        return dataBytes;
    }

    public void setDataBytes(Long dataBytes) {
        this.dataBytes = dataBytes;
    }

    public String getDataLen() {
        return dataLen;
    }

    public void setDataLen(String dataLen) {
        this.dataLen = dataLen;
    }

    public String getDataStart() {
        return dataStart;
    }

    public void setDataStart(String dataStart) {
        this.dataStart = dataStart;
    }

    public String getDataStop() {
        return dataStop;
    }

    public void setDataStop(String dataStop) {
        this.dataStop = dataStop;
    }

    public String getCrcMode() {
        return crcMode;
    }

    public void setCrcMode(String crcMode) {
        this.crcMode = crcMode;
    }

    public String getCollectCmd() {
        return collectCmd;
    }

    public void setCollectCmd(String collectCmd) {
        this.collectCmd = collectCmd;
    }

    public String getStartCmd() {
        return startCmd;
    }

    public void setStartCmd(String startCmd) {
        this.startCmd = startCmd;
    }

    public String getStopCmd() {
        return stopCmd;
    }

    public void setStopCmd(String stopCmd) {
        this.stopCmd = stopCmd;
    }

    public String getThreshold() {
        return threshold;
    }

    public void setThreshold(String threshold) {
        this.threshold = threshold;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getDataRange() {
        return dataRange;
    }

    public void setDataRange(String dataRange) {
        this.dataRange = dataRange;
    }

    public String getAlarm() {
        return alarm;
    }

    public void setAlarm(String alarm) {
        this.alarm = alarm;
    }

    public String getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(String alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDataValue() {
        return dataValue;
    }

    public void setDataValue(String dataValue) {
        this.dataValue = dataValue;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getAcquisitionPpoint() {
        return acquisitionPpoint;
    }

    public void setAcquisitionPpoint(String acquisitionPpoint) {
        this.acquisitionPpoint = acquisitionPpoint;
    }

    @Override
    public String toString() {
        return name;
    }
}
