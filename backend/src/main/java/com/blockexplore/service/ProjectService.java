package com.blockexplore.service;

import com.blockexplore.model.*;
import com.blockexplore.request.AppraisalCertInfoRequest;
import com.blockexplore.request.ConfirmTransferRequest;
import com.blockexplore.request.ReBackRequest;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProjectService {
    Result<String> createProject(Project project);

    Result<String> addUpInfo(UpInfo upInfo);

    Result<String> addTransferor(Transferor transferor);

    Result<String> addTransferee(Transferee transferee);

    Result<String> addContractInfo(ContractInfo contractInfo);

    Result<ProjectBaseInfo> getProjectBaseInfo(String projectCode) throws ABICodecException;

    Result<Object> addUpProject(UpProject upProject);

    Result<String> frontRe(String projectCode,String userId);

    Result<String> transferRe(String projectCode,String userId);

    Result<String> tradeRe(String projectCode,String userId);

    Result<String> contractRe(String projectCode,String userId);

    Result<String> confirmTransferee(ConfirmTransferRequest confirmTransferRequest);

    Result<String> addAppraisalCertInfo(AppraisalCertInfoRequest appraisalCertInfoRequest);

    Result<Object> getAppraisalCertFields(String projectCode);

    Result<Object> getTransfereeFields(String projectCode);

    Result<String> updataTransferor(Transferor transferor);
    Result<String> updateTransferee(Transferee transferee);

    Result<String> updataContractInfo(ContractInfo contractInfo);

    Result<Object> getAllProjectInfo(String projectCode) throws ABICodecException;

    Result<List<Object>> getAllProjectInfoList() throws ABICodecException;

    Result<Object> reBack(ReBackRequest reBackRequest);

    Result<Object> getFrontStepContract() throws ABICodecException;

    Result<Object> getTransStep() throws ABICodecException;

    Result<Object> getTradeStep() throws ABICodecException;

    Result<Object> getContractStep() throws ABICodecException;

    Result<Object> getEndStep() throws ABICodecException;

    Result<Object> getProjectInfoByUserName(String userName);

    Result<List<HistoryInfo>> getHistoryInfo(String projectCode) throws ABICodecException;

    Result<Object> getUpContract() throws ABICodecException;

    Result<Object> getNotUpContract() throws ABICodecException;

    Result<Object> goUpInfo(String projectCode, String userId);

    Result<String> delistProject(String projectCode, String userId);

    Result<List<Object>> getNotPubUp() throws ABICodecException;

    Result<List<Object>> getIsPubUp() throws ABICodecException;

    Result<String> extendUpEndDate(String projectCode, String newDate,String userId);

    Result<Object> getProjectInfoByIdNumber(String idNumber);

    Result<String> updateAP(String code, String area, String perimeter);

    Result<String> queryAP(String code);
}
