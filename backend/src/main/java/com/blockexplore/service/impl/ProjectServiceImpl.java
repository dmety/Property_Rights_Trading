package com.blockexplore.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.mapper.ProjectDao;
import com.blockexplore.mapper.UserDao;
import com.blockexplore.model.*;
import com.blockexplore.model.android.ProjectInfo;
import com.blockexplore.request.AppraisalCertInfoRequest;
import com.blockexplore.request.ConfirmTransferRequest;
import com.blockexplore.request.ReBackRequest;
import com.blockexplore.service.ProjectService;
import com.blockexplore.utils.*;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    ProjectDao projectDao;
    @Autowired
    UserDao userDao;
    CodeUtils codeUtils = new CodeUtils();
    private final NoUtils noUtils;

    public ProjectServiceImpl(NoUtils noUtils) {
        this.noUtils = noUtils;
    }




    @Override
    public Result<String> createProject(Project project) {
        // 设置时间
        project.setAptDate(TimeUtils.getCurrentDateTime());
        project.setCreateTime(TimeUtils.getCurrentDateTime());
        project.setEditTime(TimeUtils.getCurrentDateTime());
        project.setProjectStatus("项目受理");
        project.setSumSuccessPrice("0");

        List<Object> param = new ArrayList<>();
//        rightCommonInfo.setRightNo(noUtils.generateNo("CQ", rightCommonInfo.getType()));
        project.setProjectCode(noUtils.generateProjectNo("XM",project.getTransKind()));
        param.add(project.getProjectCode());
        param.add(project.getRightNo());
        param.add(project.getTransKind());
        param.add(Long.parseLong(project.getAptUserId()));
        param.add(Long.parseLong(project.getAptOrganId()));
        param.add(project.getAptDate());
        param.add(project.getProjectStartDate());
        param.add(project.getProjectEndDate());
//        param.add(project.getProjectStatus());

        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"createProject",param));

        if (result.getBool("statusOK")){
            projectDao.setIsPubUp(project.getProjectCode());
            projectDao.createProject(project);
        } else  {
            return Result.failure("项目创建失败");
        }
        return Result.success(project.getProjectCode());

    }

    @Override
    public Result<String> addUpInfo(UpInfo upInfo) {
        List<Object> params = new ArrayList<>();
        // 为项目初始化上架状态
        upInfo.setUpStatus("未挂牌");
        upInfo.setIsPubUp("0");
        params.add(upInfo.getProjectCode());
        params.add(upInfo.getUpStatus());
        params.add(upInfo.getUpStartDate());
        params.add(upInfo.getUpEndDate());
        params.add(Long.parseLong(upInfo.getUpPrice()));
        params.add(upInfo.getUpPriceUnit());
        params.add(upInfo.getRollOutMode());
        params.add(Long.parseLong(upInfo.getRollOutArea()));
        params.add(Long.parseLong(upInfo.getUserId()));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"addUpInfo",params));
        if (result.getBool("statusOK")){
            return Result.success("上架信息添加成功");
        } else {
            return Result.failure("上架信息添加失败");
        }
    }

    @Override
    public Result<String> addTransferor(Transferor transferor) {
        List<Object> params = new ArrayList<>();
        params.add(transferor.getUserType());
        params.add(transferor.getProjectCode());
        params.add(transferor.getTransferorName());
        params.add(transferor.getCardType());
        params.add(transferor.getTransferorCardNo());
        params.add(transferor.getOrganRegNo());
        params.add(Long.parseLong(transferor.getTelephone()));
        params.add(transferor.getEmail());
        params.add(transferor.getCondition());
        params.add(transferor.getTransMode());
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"addTransferor",params));
        if (result.getBool("statusOK")){
            projectDao.addTransferIdByCode(transferor.getProjectCode(),transferor.getTransferorCardNo());
            return Result.success("转让方信息添加成功");
        } else {
            return Result.failure("转让方信息添加失败");
        }
    }

    @Override
    public Result<String> addTransferee(Transferee transferee) {
        List<Object> params = new ArrayList<>();
        params.add(transferee.getProjectCode()); //项目编号
//        params.add(transferee.getUserAddr());
        params.add(transferee.getTransfereeName()); //受让方名称
        params.add(transferee.getCardType()); //证件类型
        params.add(transferee.getTransfereeCardNo());//证件号码
        params.add(transferee.getOrganRegNo());//组织机构注册号
        params.add(Long.parseLong(transferee.getTelephone())); //手机号码
        params.add(transferee.getEmail()); //邮箱
        params.add(Long.parseLong(transferee.getWillPrice())); //拟收价
        params.add(transferee.getWillPriceUnit()); //拟收价单位
//        params.add(transferee.getRemark());
        params.add(transferee.getUserType()); //用户类型
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"addTransferee",params));
        if (result.getBool("statusOK")){
            return Result.success("受让方信息添加成功");
        } else {
            return Result.failure("受让方信息添加失败");
        }
    }
    @Override
    public Result<String> addContractInfo(ContractInfo contractInfo) {
        String contractNo = noUtils.generateContractNo("HT",projectDao.getTypeByCode(contractInfo.getProjectCode()));
        List<Object> params = new ArrayList<>();
        params.add(contractInfo.getProjectCode());
        params.add(contractNo);
        params.add(Long.parseLong(contractInfo.getSuccessPrice()));
//        params.add(Long.parseLong(contractInfo.getSuccessPriceUnit()));
        params.add(contractInfo.getSuccessPriceUnit());
        params.add(Long.parseLong(contractInfo.getSumSuccessPrice()));
        params.add(TimeUtils.getCurrentDateTime());
        params.add(contractInfo.getContractRollOutMode());
        params.add(contractInfo.getContractRollOutStartDate());
        params.add(contractInfo.getContractRollOutEndDate());
        params.add(Long.parseLong(contractInfo.getContractRollOutArea()));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"addContractInfo",params));
        if (result.getBool("statusOK")){
            System.out.println("合同价格为："+contractInfo.getSuccessPrice());
//            更新流转信息并且填入价格和亩数
            projectDao.saveContractCode(contractInfo.getProjectCode(),contractInfo.getSumSuccessPrice(),contractInfo.getContractRollOutArea(), contractNo);
            return Result.success("合同信息添加成功");
        } else {
            return Result.failure("合同信息添加失败");
        }
    }

    @Override
    public Result<ProjectBaseInfo> getProjectBaseInfo(String projectCode) throws ABICodecException {
        List<String> params = new ArrayList<>();
        ProjectBaseInfo projectBaseInfo = new ProjectBaseInfo();
        params.add(projectCode);
        JSONObject response = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getProjectBaseInfo",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeProject("getProjectBaseInfo",response.getStr("output")));
        projectBaseInfo.setProjectCode(result.get(0));
        projectBaseInfo.setRightNo(result.get(1));
        projectBaseInfo.setTransKind(result.get(2));
        projectBaseInfo.setAptUserCode(result.get(3));
        projectBaseInfo.setAptOrganCode(result.get(4));
        projectBaseInfo.setAptDate(result.get(5));
        return Result.success(projectBaseInfo);
    }
    public ProjectBaseInfo getProjectBaseInfoEntity(String projectCode) throws ABICodecException {
        List<String> params = new ArrayList<>();
        ProjectBaseInfo projectBaseInfo = new ProjectBaseInfo();
        params.add(projectCode);
        JSONArray response = JSONUtil.parseArray(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getProjectBaseInfo",params));
//        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeProject("getProjectBaseInfo",response.getStr("output")));
        List<String> result = response.toList(String.class);
        projectBaseInfo.setProjectCode(result.get(0));
        projectBaseInfo.setRightNo(result.get(1));
        projectBaseInfo.setTransKind(result.get(2));
        projectBaseInfo.setAptUserCode(result.get(3));
        projectBaseInfo.setAptOrganCode(result.get(4));
        projectBaseInfo.setAptDate(result.get(5));
        projectBaseInfo.setProjectStartDate(result.get(6));
        projectBaseInfo.setProjectEndDate(result.get(7));
        projectBaseInfo.setProjectStatus(result.get(8));
        projectBaseInfo.setSuccessPulic(result.get(9));

        projectBaseInfo.setProjectName(projectDao.getNameByCode(projectCode));
        projectBaseInfo.setUserName(projectDao.userNameByCode(projectBaseInfo.getAptUserCode()));
        projectBaseInfo.setOrgName(projectDao.organNameByCode(projectBaseInfo.getAptOrganCode()));
        return projectBaseInfo;
    }
    @Override
    public Result<Object> addUpProject(UpProject upProject) {
        // 首先接收参数中的project信息
        JSONObject result = JSONUtil.parseObj(createProject(upProject.getProject()));
        String projectCode = result.getStr("data");
        UpInfo upInfo = upProject.getUpInfo();
        upInfo.setProjectCode(projectCode);
        addUpInfo(upInfo);
        JSONObject callback = new JSONObject();
        callback.set("projectCode",projectCode);
        callback.set("time",upInfo.getUpStartDate());
        callback.set("projectStatus","未挂牌");
        projectDao.setProjectStatus("前置审批",projectCode);
        // 存入项目流转历史
        History history = new History();
        history.setProjectCode(projectCode);
        history.setProjectStatus("项目受理");
        history.setUserId(upInfo.getUserId());
        history.setOrgId(projectDao.getUserOrgId(upInfo.getUserId()));
        history.setRejectFlag(1);
        history.setDoneTime(TimeUtils.getCurrentDateTime());
        // 保存信息到数据库
        projectDao.saveHistory(history);
        projectDao.setPubUpInit(projectCode);
//        projectDao.changeStatus("1",projectCode);
        return Result.success(callback);
    }

    @Override
    public Result<String> frontRe(String projectCode,String userId) {
        List<Object> params = new ArrayList<>();
        params.add(projectCode);
        params.add(Long.parseLong(userId));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"frontRe",params));
        if (result.getBool("statusOK")){
//            projectDao.changeStatus("2",projectCode);
            projectDao.setProjectStatus("转出审核",projectCode);
            // 插入项目流转历史
            History history = new History();
            history.setProjectCode(projectCode);
            history.setProjectStatus("前置审批");
            history.setUserId(userId);
            history.setOrgId(projectDao.getUserOrgId(userId));
            history.setRejectFlag(1);
            history.setDoneTime(TimeUtils.getCurrentDateTime());
            projectDao.saveHistory(history);
            return Result.success("前置审批成功");
        } else {
            return Result.failure("前置审批失败");
        }
        }

    @Override
    public Result<String> transferRe(String projectCode,String userId) {
        List<Object> params = new ArrayList<>();
        params.add(projectCode);
        params.add(Long.parseLong(userId));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"transferRe",params));
        if (result.getBool("statusOK")){
//            projectDao.changeStatus("3",projectCode);
            projectDao.setProjectStatus("受让受理",projectCode);
            History history = new History();
            history.setProjectCode(projectCode);
            history.setProjectStatus("转出审核");
            history.setUserId(userId);
            history.setOrgId(projectDao.getUserOrgId(userId));
            history.setRejectFlag(1);
            history.setDoneTime(TimeUtils.getCurrentDateTime());
            projectDao.saveHistory(history);
            return Result.success("转出审批成功");
        } else {
            return Result.failure("转出审批失败");
        }
    }

    @Override
    public Result<String> tradeRe(String projectCode,String userId) {
        List<Object> params = new ArrayList<>();
        params.add(projectCode);
        params.add(Long.parseLong(userId));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"tradeRe",params));
        if (result.getBool("statusOK")){
            projectDao.setProjectStatus("合同签订",projectCode);
            History history = new History();
            history.setProjectCode(projectCode);
            history.setProjectStatus("受让审核");
            history.setUserId(userId);
            history.setOrgId(projectDao.getUserOrgId(userId));
            history.setRejectFlag(1);
            history.setDoneTime(TimeUtils.getCurrentDateTime());
            projectDao.saveHistory(history);
            return Result.success("交易审批成功");
        } else {
            return Result.failure("交易审批失败");
        }
    }

    @Override
    public Result<String> contractRe(String projectCode,String userId) {
        List<Object> params = new ArrayList<>();
        params.add(projectCode);
        params.add(Long.parseLong(userId));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"contractRe",params));
        if (result.getBool("statusOK")){
//            projectDao.changeStatus("0",projectCode);
            projectDao.setProjectStatus("鉴证打印",projectCode);
            History history = new History();
            history.setProjectCode(projectCode);
            history.setProjectStatus("合同签订");
            history.setUserId(userId);
            history.setOrgId(projectDao.getUserOrgId(userId));
            history.setRejectFlag(1);
            history.setDoneTime(TimeUtils.getCurrentDateTime());
            projectDao.saveHistory(history);
            return Result.success("合同审批成功");
        } else {
            return Result.failure("合同审批失败");
        }
    }

    @Override
    public Result<String> confirmTransferee(ConfirmTransferRequest confirmTransferRequest) {
        List<Object> params = new ArrayList<>();
        params.add(confirmTransferRequest.getProjectCode());
        params.add(confirmTransferRequest.getTransfereeCardNo());
        params.add(Long.parseLong(confirmTransferRequest.getUserId()));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"confirmTransferee",params));
        if (result.getBool("statusOK")){
            projectDao.addTransfereeIdByCode(confirmTransferRequest.getProjectCode(),confirmTransferRequest.getTransfereeCardNo());
            projectDao.setProjectStatus("受让审核",confirmTransferRequest.getProjectCode());
            History history = new History();
            history.setProjectCode(confirmTransferRequest.getProjectCode());
            history.setProjectStatus("受让受理");
            history.setUserId(confirmTransferRequest.getUserId());
            history.setOrgId(projectDao.getUserOrgId(confirmTransferRequest.getUserId()));
            history.setRejectFlag(1);
            history.setDoneTime(TimeUtils.getCurrentDateTime());
            projectDao.saveHistory(history);
            return Result.success("受让方确认成功");
        } else {
            return Result.failure("受让方确认失败");
        }
    }

    @Override
    public Result<String> addAppraisalCertInfo(AppraisalCertInfoRequest appraisalCertInfoRequest) {
        List<String> params = new ArrayList<>();
        params.add(appraisalCertInfoRequest.getProjectCode());
        params.add(appraisalCertInfoRequest.getAppraisalCertNo());
        params.add(appraisalCertInfoRequest.getAppraisalDate());
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"addAppraisalCertInfo",params));
        if (result.getBool("statusOK")){
            return Result.success("评估书信息添加成功");
        } else {
            return Result.failure("评估书信息添加失败");
        }
    }

    @Override
    public Result<Object> getAppraisalCertFields(String projectCode) {
        List<String> params = new ArrayList<>();
        params.add(projectCode);
        JSONObject response = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getAppraisalCertFields",params));
        JSONArray output = JSONUtil.parseArray(response);
        List<String> result = output.toList(String.class);
        JSONObject callback = new JSONObject();
        callback.set("appraisalCertNo",result.get(0));
        callback.set("appraisalDate",result.get(1));
        return Result.success(callback);
    }

    @Override
    public Result<Object> getTransfereeFields(String address) {
        List<String> params = new ArrayList<>();
        params.add(address);
        System.out.println("getTransfereeFieldsAddress ===> " + address);
        String content = HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getTransfereeFields",params);
        JSONArray response = JSONUtil.parseArray(content);
//        JSONArray output = JSONUtil.parseArray(response);
        List<String> result = response.toList(String.class);
        JSONObject callback = new JSONObject();
//        callback.set("id",result.get(0));
//        callback.set("userAddr",result.get(1));
        callback.set("transfereeName",result.get(0));
        callback.set("cardType",result.get(1));
        callback.set("transfereeCardNo",result.get(2));
        callback.set("organRegNo",result.get(3));
        callback.set("telephone",result.get(4));
        callback.set("email",result.get(5));
        callback.set("willPrice",result.get(6));
        callback.set("willPriceUnit",result.get(7));
//        callback.set("remark",result.get(9));
        callback.set("status",result.get(8));
        callback.set("userType",result.get(9));
        return Result.success(callback);

    }
    public Object getTransferee(String idCard) {
        List<String> params = new ArrayList<>();
        params.add(idCard);
        if (idCard.equals("")){
            return null;
        }
        System.out.println("getTransfereeFieldsAddress ===> " + idCard);
        String content = HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getTransfereeFields",params);
        JSONArray response = JSONUtil.parseArray(content);
//        JSONArray output = JSONUtil.parseArray(response);
        List<String> result = response.toList(String.class);
        JSONObject callback = new JSONObject();
//        callback.set("id",result.get(0));
//        callback.set("userAddr",result.get(1));
        callback.set("transfereeName",result.get(0));
        callback.set("cardType",result.get(1));
        callback.set("transfereeCardNo",result.get(2));
        callback.set("organRegNo",result.get(3));
        callback.set("telephone",result.get(4));
        callback.set("email",result.get(5));
        callback.set("willPrice",result.get(6));
        callback.set("willPriceUnit",result.get(7));
//        callback.set("remark",result.get(8));
        callback.set("status",result.get(8));
        callback.set("userType",result.get(9));
        return callback;

    }

    @Override
    public Result<String> updataTransferor(Transferor transferor) {
        List<String> params = new ArrayList<>();
        params.add(transferor.getProjectCode());
        params.add(transferor.getUserAddr());
        params.add(transferor.getTransferorName());
        params.add(transferor.getCardType());
        params.add(transferor.getTransferorCardNo());
        params.add(transferor.getOrganRegNo());
        params.add(transferor.getTelephone());
        params.add(transferor.getEmail());
        params.add(transferor.getCondition());
        params.add(transferor.getOtherMatter());
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"updataTransferor",params));
        if (result.getBool("statusOK")){
            return Result.success("转让方信息更新成功");
        } else {
            return Result.failure("转让方信息更新失败");
        }
    }
    public Result<String> updateTransferee(Transferee transferee) {
        List<String> params = new ArrayList<>();
        params.add(transferee.getProjectCode());
        params.add(transferee.getUserAddr());
        params.add(transferee.getTransfereeName());
        params.add(transferee.getCardType());
        params.add(transferee.getTransfereeCardNo());
        params.add(transferee.getOrganRegNo());
        params.add(transferee.getTelephone());
        params.add(transferee.getEmail());
        params.add(transferee.getWillPrice());
        params.add(transferee.getWillPriceUnit());
        params.add(transferee.getRemark());
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"updataTransferee",params));
        if (result.getBool("statusOK")){
            return Result.success("受让方信息添加成功");
        } else {
            return Result.failure("受让方信息添加失败");
        }
    }

    @Override
    public Result<String> updataContractInfo(ContractInfo contractInfo) {
        List<String> params = new ArrayList<>();
        params.add(contractInfo.getProjectCode());
        params.add(contractInfo.getContractCode());
        params.add(contractInfo.getSuccessPrice());
        params.add(contractInfo.getSuccessPriceUnit());
        params.add(contractInfo.getSumSuccessPrice());
        params.add(contractInfo.getContractDate());
        params.add(contractInfo.getContractRollOutMode());
        params.add(contractInfo.getContractRollOutStartDate());
        params.add(contractInfo.getContractRollOutEndDate());
        params.add(contractInfo.getContractRollOutArea());
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"addContractInfo",params));
        if (result.getBool("statusOK")){
            projectDao.setProjectStatus("合同审核", contractInfo.getProjectCode());
            return Result.success("合同信息更新成功");
        } else {
            return Result.failure("合同信息更新失败");
        }
    }

    @Override
    public Result<Object> getAllProjectInfo(String projectCode) throws ABICodecException {
        ProjectBaseInfo baseInfo =  getProjectBaseInfoEntity(projectCode);
        UpInfo upInfo = getUpInfoEntity(projectCode);
        ContractInfo contractInfo = getContractInfoEntity(projectCode);
        Transferor transferor = getTransferorEntity(projectCode);
        List<String> address = getTransfereeIdCards(projectCode);
        List<Object> transferee = new ArrayList<>();
        for (String addr : address){
            transferee.add(getTransferee(addr));
        }
        JSONObject result = new JSONObject();
        result.set("baseInfo",baseInfo);
        result.set("upInfo",upInfo);
        result.set("contractInfo",contractInfo);
        result.set("transferor",transferor);
        result.set("transferee",transferee);
        return Result.success(result);
    }
    public Object getAllProject(String projectCode) throws ABICodecException {
        ProjectBaseInfo baseInfo =  getProjectBaseInfoEntity(projectCode);
        UpInfo upInfo = getUpInfoEntity(projectCode);
        ContractInfo contractInfo = getContractInfoEntity(projectCode);
        Transferor transferor = getTransferorEntity(projectCode);
        List<String> address = getTransfereeIdCards(projectCode);
        List<Object> transferee = new ArrayList<>();
        for (String addr : address){
            transferee.add(getTransferee(addr));
        }
        JSONObject result = new JSONObject();
        result.set("baseInfo",baseInfo);
        result.set("upInfo",upInfo);
        result.set("contractInfo",contractInfo);
        result.set("transferor",transferor);
        result.set("transferee",transferee);
        return result;
    }

    @Override
    public Result<List<Object>> getAllProjectInfoList() throws ABICodecException {
        List<String> projectCodes =  projectDao.getAllProjectCode();
        List<Object> results = new ArrayList<>();
        for (String projectCode : projectCodes){
            results.add(getAllProject(projectCode));
        }
        return Result.success(results);
    }

    @Override
    public Result<Object> reBack(ReBackRequest reBackRequest) {
        List<Object> params = new ArrayList<>();
        params.add(reBackRequest.getProjectCode());
        params.add(reBackRequest.getNowStep());
        params.add(reBackRequest.getStep());
        params.add(reBackRequest.getUserId());
        params.add(reBackRequest.getProjectStatus());
        JSONObject result  = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"reBack",params));
        if (result.getBool("statusOK")){
            History history = new History();
            history.setProjectCode(reBackRequest.getProjectCode());
            history.setUserId(String.valueOf(reBackRequest.getUserId()));
            history.setOrgId(projectDao.getUserOrgId(history.getUserId()));
            history.setProjectStatus("回退");
            history.setRejectFlag(0);
            history.setRemark(String.valueOf(reBackRequest.getStep()));
            projectDao.saveHistory(history);
            return Result.success("回退成功");
        } else {
            return Result.failure("回退失败");
        }

    }

    @Override
    public Result<Object> getFrontStepContract() throws ABICodecException {
        List<String> params = new ArrayList<>();
        // 空参
//        解码
        JSONObject content = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getFronStep",params));
        System.out.println("getFrontStepContractOutputResult ===>" + codeUtils.decodeProject("getFronStep",content.getStr("output")));
        List<Object> res = codeUtils.decodeProject("getFronStep",content.getStr("output"));
        List<String> codelist = CodeUtils.convertToStringList(res);
        List<Object> result = new ArrayList<>();
        if (codelist == null){
            return Result.failure("获取失败");
        } else {
            for (String s : codelist){
                result.add(getAllProject(s));
            }
            return Result.success(result);
        }
    }

    @Override
    public Result<Object> getTransStep() throws ABICodecException {
        List<String> params = new ArrayList<>();
        // 空参
//        解码
        JSONObject content = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getTransStep",params));
//        System.out.println("getFrontStepContractOutputResult ===>" + codeUtils.decodeProject("getFronStep",content.getStr("output")));
        List<Object> res = codeUtils.decodeProject("getTransStep",content.getStr("output"));
        List<String> codelist = CodeUtils.convertToStringList(res);
        List<Object> result = new ArrayList<>();
        if (codelist == null){
            return Result.failure("获取失败");
        } else {
            for (String s : codelist){
                result.add(getAllProject(s));
            }
            return Result.success(result);
        }
    }

    @Override
    public Result<Object> getTradeStep() throws ABICodecException {
        List<String> params = new ArrayList<>();
        // 空参
//        解码
        JSONObject content = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getTradeStep",params));
//        System.out.println("getFrontStepContractOutputResult ===>" + codeUtils.decodeProject("getFronStep",content.getStr("output")));
        List<Object> res = codeUtils.decodeProject("getTradeStep",content.getStr("output"));
        List<String> codelist = CodeUtils.convertToStringList(res);
        List<Object> result = new ArrayList<>();
        if (codelist == null){
            return Result.failure("获取失败");
        } else {
            for (String s : codelist){
                result.add(getAllProject(s));
            }
            return Result.success(result);
        }
    }
    public List<String> getTradeCodeList() throws ABICodecException {
        List<String> params = new ArrayList<>();
        // 空参
//        解码
        JSONObject content = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getTradeStep",params));
//        System.out.println("getFrontStepContractOutputResult ===>" + codeUtils.decodeProject("getFronStep",content.getStr("output")));
        List<Object> res = codeUtils.decodeProject("getTradeStep",content.getStr("output"));
        List<String> codelist = CodeUtils.convertToStringList(res);
        return codelist;
    }

    @Override
    public Result<Object> getContractStep() throws ABICodecException {
        List<String> params = new ArrayList<>();
        // 空参
//        解码
        JSONObject content = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getContractStep",params));
//        System.out.println("getFrontStepContractOutputResult ===>" + codeUtils.decodeProject("getFronStep",content.getStr("output")));
        List<Object> res = codeUtils.decodeProject("getContractStep",content.getStr("output"));
        List<String> codelist = CodeUtils.convertToStringList(res);
        List<Object> result = new ArrayList<>();
        if (codelist == null){
            return Result.failure("获取失败");
        } else {
            for (String s : codelist){
                result.add(getAllProject(s));
            }
            return Result.success(result);
        }
    }

    @Override
    public Result<Object> getEndStep() throws ABICodecException {
        List<String> params = new ArrayList<>();
        // 空参
//        解码
        JSONObject content = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getEndStep",params));
//        System.out.println("getFrontStepContractOutputResult ===>" + codeUtils.decodeProject("getFronStep",content.getStr("output")));
        List<Object> res = codeUtils.decodeProject("getEndStep",content.getStr("output"));
        List<String> codelist = CodeUtils.convertToStringList(res);
        List<Object> result = new ArrayList<>();
        if (codelist == null){
            return Result.failure("获取失败");
        } else {
            for (String s : codelist){
                result.add(getAllProject(s));
            }
            return Result.success(result);
        }
    }
    public List<String> getEndContract() throws ABICodecException {
        List<String> params = new ArrayList<>();
        // 空参
//        解码
        JSONObject content = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getEndStep",params));
//        System.out.println("getFrontStepContractOutputResult ===>" + codeUtils.decodeProject("getFronStep",content.getStr("output")));
        List<Object> res = codeUtils.decodeProject("getEndStep",content.getStr("output"));
        List<String> codelist = CodeUtils.convertToStringList(res);
        return codelist;
    }

    @Override
    public Result<Object> getProjectInfoByUserName(String userName) {
        String userId = userDao.getIdByName(userName);
        List<ProjectInfo> codelist = projectDao.getProjectInfoByUserId(userId);
        return Result.success(codelist);
    }

    @Override
    public Result<List<HistoryInfo>> getHistoryInfo(String projectCode) throws ABICodecException {
        // 获取项目的历史记录编号列表
        List<String> codelist = getRightHistory(projectCode);
        Collections.reverse(codelist);
        List<HistoryInfo> infos = new ArrayList<>();

        for (String s : codelist) {
            try {
                // 创建历史信息对象
                HistoryInfo historyInfo = new HistoryInfo();

                // 将编号转为Long并添加到参数列表
                List<Object> params = new ArrayList<>();
                params.add(Long.parseLong(s));

                // 发起HTTP请求，获取历史记录信息
                JSONObject content = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS, "getHistoryInfo", params));
                System.out.println("getHistoryInfo ===>" + codeUtils.decodeRight("getHistoryInfo", content.getStr("output")));
                // 解析返回的output数据，确保JSON结构正确
//                List<String> result = JSONUtil.parseArray(codeUtils.decodeRight("getHistoryInfo", content.getStr("output")))
//                        .getJSONArray(0).toList(String.class);
                JSONArray output = JSONUtil.parseArray(codeUtils.decodeRight("getHistoryInfo", content.getStr("output")));
                List<String> result = output.toList(String.class);

                // 设置历史信息对象的各个字段
                historyInfo.setStep(result.get(0));
                historyInfo.setDoneTime(result.get(1));
                historyInfo.setDoneOrgId(result.get(2));
                historyInfo.setDoneUserId(result.get(3));
                historyInfo.setProjectCode(result.get(4));
                historyInfo.setRejectFlag(result.get(5));
                historyInfo.setOrgName(projectDao.getOrgNameById(historyInfo.getDoneOrgId()));
                historyInfo.setUserName(projectDao.getUserNameById(historyInfo.getDoneUserId()));

                // 添加到历史信息列表中
                infos.add(historyInfo);

            } catch (NumberFormatException e) {
                // 处理编号转换为Long时可能抛出的异常
                System.err.println("Invalid project code in history list: " + s);
                continue;
            } catch (Exception e) {
                // 处理其他可能的异常，包括JSON解析和HTTP请求错误
                System.err.println("Error occurred while fetching history info: " + e.getMessage());
                continue;
            }
        }

        // 返回封装了历史信息的结果
        return Result.success(infos);
    }

    @Override
    public Result<Object> getUpContract() throws ABICodecException {
        List<String> codelist = projectDao.getUpContractCode();
        List<Object> result = new ArrayList<>();
        for (String s : codelist) {
            result.add(getAllProject(s));
        }
        return Result.success(result);
    }

    @Override
    public Result<Object> getNotUpContract() throws ABICodecException {
        // 获取所有已经结束的合同
        List<String> allEndContract = getEndContract();
        // 获取还未上传的合同代码
        List<String> codelist = projectDao.getNotUpContractCode();

        // 使用 retainAll 方法找到 codelist 和 allEndContract 的交集
        codelist.retainAll(allEndContract);

        List<Object> result = new ArrayList<>();
        // 遍历交集的合同代码并获取项目数据
        for (String code : codelist) {
            result.add(getAllProject(code));
        }

        // 返回结果
        return Result.success(result);
    }


    @Override
    public Result<Object> goUpInfo(String projectCode, String userId) {
        List<Object> params = new ArrayList<>();
        params.add(projectCode);
        params.add(Long.parseLong(userId));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"goUpInfo",params));
        if (result.getBool("statusOK")){
            projectDao.updateIsPubUp(projectCode);
            return Result.success("上牌申请成功");
        } else {
            return Result.failure("上牌申请失败");
        }
    }

    @Override
    public Result<String> delistProject(String projectCode,String userId) {
        List<String> params = new ArrayList<>();
        params.add(projectCode);
        params.add(userId);
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"delistProject",params));
        if (result.getBool("statusOK")){
            projectDao.deListProject(projectCode);
            return Result.success("项目下架成功");
        } else {
            return Result.failure("项目下架失败");
        }
    }
    @Override
    public Result<String> extendUpEndDate(String projectCode, String newDate,String userId) {
        List<Object> params = new ArrayList<>();
        params.add(projectCode);
        params.add(newDate);
        params.add(Long.parseLong(userId));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"extendUpEndDate",params));
        if (result.getBool("statusOK")){
            return Result.success("延牌信息添加成功");
        } else {
            return Result.failure("延牌信息添加失败");
        }

    }

    @Override
    public Result<String> updateAP(String code, String area, String perimeter) {
        List<String> params = new ArrayList<>();
        params.add(code);
        params.add(area);
        params.add(perimeter);
        String response = HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS, "updateAP", params);

        JSONObject resultJson = JSONUtil.parseObj(response);
        if (resultJson.getInt("code") != 0) {
            return Result.failure("链上更新失败：" + resultJson.getStr("message"));
        }

        // 5. 本地数据库同步更新
        int updated = projectDao.updateAP(code, area, perimeter);
        if (updated == 0) {
            return Result.failure("本地数据库更新失败");
        }

        return Result.success("更新成功");
    }

    @Override
    public Result<String> queryAP(String code) {
        List<String> params = new ArrayList<>();
        params.add(code);
        String response = HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS, "queryAP", params);
        JSONObject resultJson = JSONUtil.parseObj(response);

        if (resultJson.getInt("code") != 0) {
            return Result.failure("链上查询失败：" + resultJson.getStr("message"));
        }

        // WeBASE 会将返回结果放在 output 中的数组里
        JSONArray output = resultJson.getJSONArray("output");
        String area = output.getStr(0);
        String perimeter = output.getStr(1);

        Map<String, String> data = new HashMap<>();
        data.put("area", area);
        data.put("perimeter", perimeter);
        return Result.success(JSONUtil.toJsonStr(data));
    }

    @Override
    public Result<List<Object>> getNotPubUp() throws ABICodecException {
        List<String> trades = getTradeCodeList();
        List<String> codelist = projectDao.getNotPubUp();
        List<Object> result = new ArrayList<>();
        codelist.retainAll(trades);
        for (String s : codelist){
            result.add(getAllProject(s));
        }
        return Result.success(result);
    }

    @Override
    public Result<List<Object>> getIsPubUp() throws ABICodecException {
        List<String> codelist = projectDao.IsPubUp();
        List<Object> result = new ArrayList<>();
        for (String s : codelist){
            result.add(getAllProject(s));
        }
        return Result.success(result);
    }

    public List<String> getRightHistory(String projectCode) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        params.add(projectCode);
        JSONObject content = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getRightHisory",params));
        System.out.println("getRightHistoryResult ===>" + codeUtils.decodeRight("getRightHisory",content.getStr("output")));
        JSONArray codelist = JSONUtil.parseArray(codeUtils.decodeRight("getRightHisory",content.getStr("output")));
        List<String> result = JSONUtil.parseArray(codelist.get(0)).toList(String.class);
//        List<Object> result = new ArrayList<>();
        if (codelist == null){
            return null;
        } else {
            return result;
        }
    }

    private Transferee getTransfereeByAddress(String address) {
        List<String> params = new ArrayList<>();
        params.add(address);
        Transferee transferee = new Transferee();
        JSONArray response = JSONUtil.parseArray(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getTransfereeFields",params));
        List<String> result = response.toList(String.class);
        transferee.setId(result.get(0));
        transferee.setUserAddr(result.get(1));
        transferee.setTransfereeName(result.get(2));
        transferee.setCardType(result.get(3));
        transferee.setTransfereeCardNo(result.get(4));
        transferee.setOrganRegNo(result.get(5));
        transferee.setTelephone(result.get(6));
        transferee.setEmail(result.get(7));
        transferee.setWillPrice(result.get(8));
        transferee.setWillPriceUnit(result.get(9));
        transferee.setRemark(result.get(10));
        transferee.setStatus(result.get(11));
        return transferee;

    }

    private Transferor getTransferorEntity(String projectCode) {
        List<String> params = new ArrayList<>();
        params.add(projectCode);
        Transferor transferor = new Transferor();
        JSONArray response = JSONUtil.parseArray(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getTransferorFields",params));
        List<String> result = response.toList(String.class);
        transferor.setProjectCode(projectCode);
//        transferor.setUserAddr(result.get(0));
        transferor.setUserType(result.get(0));
//        transferor.setLegalRepresentative(result.get(1));
        transferor.setTransferorName(result.get(1));
        transferor.setCardType(result.get(2));
        transferor.setTransferorCardNo(result.get(3));
        transferor.setOrganRegNo(result.get(4));
        transferor.setTelephone(result.get(5));
        transferor.setEmail(result.get(6));
        transferor.setCondition(result.get(7));
        transferor.setTransMode(result.get(8));
        return transferor;
    }

    private ContractInfo getContractInfoEntity(String projectCode) {
        List<String> params = new ArrayList<>();
        params.add(projectCode);
        ContractInfo contractInfo = new ContractInfo();
        JSONArray response = JSONUtil.parseArray(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getContractInfoFields",params));
        List<String> result = response.toList(String.class);
        contractInfo.setProjectCode(projectCode);
        contractInfo.setContractCode(result.get(0));
        contractInfo.setSuccessPrice(result.get(1));
        contractInfo.setSuccessPriceUnit(result.get(2));
        contractInfo.setSumSuccessPrice(result.get(3));
        contractInfo.setContractDate(result.get(4));
        contractInfo.setContractRollOutMode(result.get(5));
        contractInfo.setContractRollOutStartDate(result.get(6));
        contractInfo.setContractRollOutEndDate(result.get(7));
        contractInfo.setContractRollOutArea(result.get(8));
        return contractInfo;

    }

    private UpInfo getUpInfoEntity(String projectCode) {
        List<String> params = new ArrayList<>();
        params.add(projectCode);
        UpInfo upInfo = new UpInfo();
        String content = HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getUpInfoFields",params);
        JSONArray response = JSONUtil.parseArray(content);
        List<String> result = response.toList(String.class);
        upInfo.setProjectCode(result.get(0));
        upInfo.setUpStatus(result.get(1));
        upInfo.setUpStartDate(result.get(2));
        upInfo.setUpEndDate(result.get(3));
        upInfo.setUpPrice(result.get(4));
        upInfo.setUpPriceUnit(result.get(5));
        upInfo.setRollOutMode(result.get(6));
        upInfo.setRollOutArea(result.get(7));
        upInfo.setIsPubUp(result.get(8));
        return upInfo;
    }
    public List<String> getTransfereeIdCards(String projectCode){
        List<String> params = new ArrayList<>();
        params.add(projectCode);
        List<String> response = CodeUtils.processStringData(HttpUtils.projectRequest(EnvConfig.ADMIN_ADDRESS,"getTransfereeAddr",params));
        System.out.println("TransfereeExample ===>" + response);
//        List<String> result = response.toList(String.class);
        return response;
    }

    @Override
    public Result<Object> getProjectInfoByIdNumber(String idNumber) {
        List<ProjectInfo> codelist = projectDao.getProjectInfoByIdNumber(idNumber);
        for (ProjectInfo projectInfo : codelist){
            switch (projectInfo.getTransKind()) {
                case "A":
                    projectInfo.setTransKind("土地");
                    break;
                case "B":
                    projectInfo.setTransKind("林地");
                    break;
                case "C":
                    projectInfo.setTransKind("房屋");
                    break;
                case "D":
                    projectInfo.setTransKind("生产设施");
                    break;
                case "E":
                    projectInfo.setTransKind("知识产权");
                    break;
                default:
                    projectInfo.setTransKind("未知");
                    break;
            }
        }
        return Result.success(codelist);
    }
}
//    JSONObject result = JSONUtil.parseObj(addCommonInfo(facilityAll.getRightCommonInfo()));
//    String rightNo = result.getStr("data");
//    RightAddress rightAddress = facilityAll.getRightAddress();
//    FacilityDetail facilityDetail = facilityAll.getFacilityDetail();
//    // 设置RightNo值
//        rightAddress.setRightNo(rightNo);
//        facilityDetail.setRightNo(rightNo);
//    // 调用方法
//    fillRightAddress(rightAddress);
//    fillFacilityDetails(facilityDetail);
//        return Result.success("添加成功");
