package com.blockexplore.controller;

import com.blockexplore.model.*;
import com.blockexplore.request.AppraisalCertInfoRequest;
import com.blockexplore.request.ConfirmTransferRequest;
import com.blockexplore.request.ReBackRequest;
import com.blockexplore.service.ProjectService;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;

    // 创建项目
    @RequestMapping("/createProject")
    public Result<String> createProject(@RequestBody Project project) {
        return projectService.createProject(project);
    }

    // 添加上链信息
    @RequestMapping(value = "/addUpInfo")
    public Result<String> addUpInfo(@RequestBody UpInfo upInfo) {
        return projectService.addUpInfo(upInfo);
    }

    // 添加上链项目
    @RequestMapping("/addUpProject")
    public Result<Object> addUpProject(@RequestBody UpProject upProject) {
        return projectService.addUpProject(upProject);
    }

    // 添加转让方
    @RequestMapping("/addTransferor")
    public Result<String> addTransferor(@RequestBody Transferor transferor) {
        return projectService.addTransferor(transferor);
    }

    // 添加受让方
    @RequestMapping("/addTransferee")
    public Result<String> addTransferee(@RequestBody Transferee transferee) {
        return projectService.addTransferee(transferee);
    }

    // 添加合同信息
    @RequestMapping("/addContractInfo")
    public Result<String> addContractInfo(@RequestBody ContractInfo contractInfo) {
        return projectService.addContractInfo(contractInfo);
    }

    // 获取项目基础信息
    @RequestMapping("/getProjectBaseInfo")
    public Result<ProjectBaseInfo> getProjectBaseInfo(@RequestParam("projectCode") String projectCode) throws ABICodecException {
        return projectService.getProjectBaseInfo(projectCode);
    }

    // 前端确认操作
    @RequestMapping("/frontRe")
    public Result<String> frontRe(@RequestParam("projectCode") String projectCode, @RequestParam("userId") String userId) {
        return projectService.frontRe(projectCode, userId);
    }

    // 转移确认操作
    @RequestMapping("/transferRe")
    public Result<String> transferRe(@RequestParam("projectCode") String projectCode, @RequestParam("userId") String userId) {
        return projectService.transferRe(projectCode, userId);
    }

    // 交易确认操作
    @RequestMapping("/tradeRe")
    public Result<String> tradeRe(@RequestParam("projectCode") String projectCode, @RequestParam("userId") String userId) {
        return projectService.tradeRe(projectCode, userId);
    }

    // 合同确认操作
    @RequestMapping("/contractRe")
    public Result<String> contractRe(@RequestParam("projectCode") String projectCode, @RequestParam("userId") String userId) {
        return projectService.contractRe(projectCode, userId);
    }

    // 确认受让方
    @RequestMapping("/confirmTransferee")
    public Result<String> confirmTransferee(@RequestBody ConfirmTransferRequest confirmTransferRequest) {
        return projectService.confirmTransferee(confirmTransferRequest);
    }

    // 添加评估证书信息
    @RequestMapping("/addAppraisalCertInfo")
    public Result<String> addAppraisalCertInfo(@RequestBody AppraisalCertInfoRequest appraisalCertInfoRequest) {
        return projectService.addAppraisalCertInfo(appraisalCertInfoRequest);
    }

    // 获取评估证书字段
    @RequestMapping("/getAppraisalCertFields")
    public Result<Object> getAppraisalCertFields(@RequestParam("projectCode") String projectCode) {
        return projectService.getAppraisalCertFields(projectCode);
    }

    // 获取受让方字段
    @RequestMapping("/getTransfereeFields")
    public Result<Object> getTransfereeFields(@RequestParam("projectCode") String projectCode) {
        return projectService.getTransfereeFields(projectCode);
    }

    // 更新转让方信息
    @RequestMapping("/updateTransferor")
    public Result<String> updateTransferor(@RequestBody Transferor transferor) {
        return projectService.updataTransferor(transferor);
    }

    // 更新受让方信息
    @RequestMapping("/updateTransferee")
    public Result<String> updateTransferee(@RequestBody Transferee transferee) {
        return projectService.updateTransferee(transferee);
    }

    // 更新合同信息
    @RequestMapping("/updateContractInfo")
    public Result<String> updataContractInfo(@RequestBody ContractInfo contractInfo) {
        return projectService.updataContractInfo(contractInfo);
    }

    // 获取所有项目信息
    @RequestMapping("/getAllProjectInfo")
    public Result<Object> getAllProjectInfo(@RequestParam("projectCode") String projectCode) throws ABICodecException {
        return projectService.getAllProjectInfo(projectCode);
    }

    // 获取所有项目信息列表
    @RequestMapping("/getAllProjectInfoList")
    public Result<List<Object>> getAllProjectInfoList() throws ABICodecException {
        return projectService.getAllProjectInfoList();
    }

    // 回退操作
    @RequestMapping("/reBack")
    public Result<Object> reBack(@RequestBody ReBackRequest reBackRequest) {
        return projectService.reBack(reBackRequest);
    }

    // 获取前端步骤合同
    @RequestMapping("/getFrontStep")
    public Result<Object> getFrontStepContract() throws ABICodecException {
        return projectService.getFrontStepContract();
    }

    // 获取转移步骤
    @RequestMapping("/getTransStep")
    public Result<Object> getTransStep() throws ABICodecException {
        return projectService.getTransStep();
    }

    // 获取交易步骤
    @RequestMapping("/getTradeStep")
    public Result<Object> getTradeStep() throws ABICodecException {
        return projectService.getTradeStep();
    }

    // 获取合同步骤
    @RequestMapping("/getContractStep")
    public Result<Object> getContractStep() throws ABICodecException {
        return projectService.getContractStep();
    }

    // 获取结束步骤
    @RequestMapping("/getEndStep")
    public Result<Object> getEndStep() throws ABICodecException {
        return projectService.getEndStep();
    }

    // 根据用户名获取项目信息
    @RequestMapping("/getProjectInfoByUserName")
    public Result<Object> getProjectInfoByUserName(@RequestParam("userName") String userName) throws ABICodecException {
        return projectService.getProjectInfoByUserName(userName);
    }

    // 根据身份证号查询参与的项目
    @RequestMapping("/getProjectInfoByIdNumber")
    public Result<Object> getProjectInfoByIdNumber(@RequestParam("idNumber") String idNumber) throws ABICodecException {
        return projectService.getProjectInfoByIdNumber(idNumber);
    }

    // 获取历史信息
    @RequestMapping("/getHistoryInfo")
    public Result<List<HistoryInfo>> getHistoryInfo(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return projectService.getHistoryInfo(rightNo);
    }

    // 获取上传合同
    @GetMapping("/getUpContract")
    public Result<Object> getUpContract() throws ABICodecException {
        return projectService.getUpContract();
    }

    // 获取未上传合同
    @GetMapping("/getNotUpContract")
    public Result<Object> getNotUpContract() throws ABICodecException {
        return projectService.getNotUpContract();
    }

    // 上链信息
    @RequestMapping("/goUpInfo")
    public Result<Object> goUpInfo(@RequestParam("projectCode") String projectCode, @RequestParam("userId") String userId) throws ABICodecException {
        return projectService.goUpInfo(projectCode, userId);
    }

    // 下架项目
    @RequestMapping("/delistProject")
    public Result<String> delistProject(@RequestParam("projectCode") String projectCode, @RequestParam("userId") String userId) {
        return projectService.delistProject(projectCode, userId);
    }

    // 延长上链截止日期
    @RequestMapping("/extendUpEndDate")
    public Result<String> extendUpEndDate(@RequestParam("projectCode") String projectCode, @RequestParam("newDate") String newDate, @RequestParam("userId") String userId) {
        return projectService.extendUpEndDate(projectCode, newDate, userId);
    }

    // 获取已发布上链项目
    @RequestMapping("/getIsPubUp")
    public Result<List<Object>> getIsPubUp() throws ABICodecException {
        return projectService.getIsPubUp();
    }

    // 获取未发布上链项目
    @RequestMapping("/getNotPubUp")
    public Result<List<Object>> getNotPubUp() throws ABICodecException {
        return projectService.getNotPubUp();
    }

    // 更新面积和周长
    @PostMapping("/updateAP")
    public Result<String> updateAP(@RequestParam("projectCode") String code, @RequestParam("area") String area, @RequestParam("perimeter") String perimeter) {
        return projectService.updateAP(code, area, perimeter);
    }

    // 查询面积和周长
    @PostMapping("/queryAP")
    public Result<String> queryAP(@RequestParam("projectCode") String code) {
        return projectService.queryAP(code);
    }
}

