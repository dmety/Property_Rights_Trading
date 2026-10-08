package com.blockexplore.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.mapper.OrganDao;
import com.blockexplore.model.ContractOrgan;
import com.blockexplore.model.Organ;
import com.blockexplore.service.OrganService;
import com.blockexplore.utils.CodeUtils;
import com.blockexplore.utils.HttpUtils;
import com.blockexplore.utils.Result;
import com.blockexplore.utils.TimeUtils;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.fisco.bcos.sdk.client.protocol.response.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class OrganServiceImpl implements OrganService {
    @Autowired
    private OrganDao organDao;
    CodeUtils codeUtils = new CodeUtils();
    @Override
    public Result<String> createOrgan(Organ organ) throws ABICodecException {
        List<String> params = new  ArrayList<>();
        organ.setCreateTime(TimeUtils.getCurrentDateTime());
        organ.setEditTime(TimeUtils.getCurrentDateTime());
        if (organDao.isOrganExist(organ.getOrgCode())){
            return Result.failure("机构已存在");
        }
        params.add(organ.getOrgCode());
        params.add(organ.getOrgName());
        params.add(organ.getOrgType());
        params.add(organ.getZcode());
        params.add(organ.getTransKind());
        JSONObject result = JSONUtil.parseObj(HttpUtils.systemRequest(EnvConfig.ADMIN_ADDRESS,"createOrgan",params));
        System.out.println("CreateOrganResult ===>" + result);
        organ.setId(CodeUtils.convertToStringList(codeUtils.decode("createOrgan",result.get("output").toString())).get(0));
//        System.out.println("CreateOrganResultOutput ===>" + CodeUtils.convertToStringList(codeUtils.decode("createOrgan",result.get("output").toString())));

        if (result.getBool("statusOK")){
            organDao.createOrgan(organ);
            return Result.success("创建机构成功");
        } else {
            return Result.failure("创建机构失败");
        }
    }
//    private String id;
//    private String orgCode;
//    private String name;
//    private String orgType;
//    private String zcode;
//    private String transKind;

    @Override
    public Result<List<ContractOrgan>> getOrganList() throws ABICodecException {
        List<String> ids = organDao.getAllOrganId();
        List<ContractOrgan> contractOrgans = new ArrayList<>();
        for (String id:ids){
            List<String> params = new ArrayList<>();
            params.add(id);
            JSONObject response = JSONUtil.parseObj(HttpUtils.systemRequest(EnvConfig.ADMIN_ADDRESS,"getOrgan",params));
            List<Object> output = codeUtils.decode("getOrgan",response.getStr("output"));
            List<String> result = CodeUtils.convertToStringList(output);
            System.out.println("GetAllOrganResult ===>" + result);
            ContractOrgan contractOrgan = new ContractOrgan();
            contractOrgan.setId(result.get(0));
            contractOrgan.setOrgCode(result.get(1));
            contractOrgan.setName(result.get(2));
            contractOrgan.setOrgType(result.get(3));
            contractOrgan.setZcode(result.get(4));
            contractOrgan.setTransKind(result.get(5));
            contractOrgans.add(contractOrgan);
        }
        return Result.success(contractOrgans);

    }

    @Override
    public Result<String> deleteOrgan(String orgId) {
        List<String> params = new ArrayList<>();
        params.add(orgId);
        HttpUtils.systemRequest(EnvConfig.ADMIN_ADDRESS,"deleteOrgan",params);
        organDao.deleteOrgan(orgId);
        return Result.success("删除机构成功");
    }

    @Override
    public Result<ContractOrgan> getOrganInfoById(String orgId) throws ABICodecException {
        List<String> param = new ArrayList<>();
        param.add(orgId);
        ContractOrgan organ = new ContractOrgan();
        JSONObject response = JSONUtil.parseObj(HttpUtils.systemRequest(EnvConfig.ADMIN_ADDRESS,"getOrgan",param));
        List<Object> output = codeUtils.decode("getOrgan",response.getStr("output"));
        List<String> result = CodeUtils.convertToStringList(output);
        organ.setId(result.get(0));
        organ.setOrgCode(result.get(1));
        organ.setName(result.get(2));
        organ.setOrgType(result.get(3));
        organ.setZcode(result.get(4));
        organ.setTransKind(result.get(5));
        return Result.success(organ);

    }

    @Override
    public Result<List<Organ>> getAllOrganByType(String transKind) {
        return Result.success(organDao.getAllOrganByType(transKind));
    }
}
