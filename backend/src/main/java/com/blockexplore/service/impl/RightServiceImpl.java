package com.blockexplore.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.mapper.RightDao;
import com.blockexplore.model.*;
import com.blockexplore.request.QueryTypeRightRequest;
import com.blockexplore.service.RightService;
import com.blockexplore.utils.*;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RightServiceImpl implements RightService {
    @Autowired
    RightDao rightDao;
    CodeUtils codeUtils = new CodeUtils();
    private final NoUtils noUtils;

    public RightServiceImpl(NoUtils noUtils) {
        this.noUtils = noUtils;
    }

    @Override
    public Result<String> addCommonInfo(RightCommonInfo rightCommonInfo) {
        List<Object> params = new ArrayList<>();
//        生成产权编号
        rightCommonInfo.setRightNo(noUtils.generateNo("CQ", rightCommonInfo.getType()));
        params.add(rightCommonInfo.getRightNo());
        params.add(rightCommonInfo.getRightName());
        params.add(Long.parseLong(rightCommonInfo.getOrgId()));
        params.add(rightCommonInfo.getRightCertCode());
        params.add(rightCommonInfo.getRightOwner());
        params.add(Long.parseLong(rightCommonInfo.getUserId()));
        params.add(Long.parseLong(rightCommonInfo.getOwnerShip()));
        params.add(rightCommonInfo.getUseStartDate());
        params.add(rightCommonInfo.getUseEndDate());
        params.add(rightCommonInfo.getRightCardId());
        Right right = new Right();
        right.setRightCertCode(rightCommonInfo.getRightCertCode());
        right.setRightNo(rightCommonInfo.getRightNo());
        right.setOrganId(rightCommonInfo.getOrgId());
        right.setCreateTime(TimeUtils.getCurrentDateTime());
        right.setEditTime(TimeUtils.getCurrentDateTime());
        right.setRightName(rightCommonInfo.getRightName());
        right.setTransKind(rightCommonInfo.getType());
        right.setRightOwner(rightCommonInfo.getRightOwner());
        right.setRightCardId(rightCommonInfo.getRightCardId());

        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"addCommonInfo",params));
        right.setBlockNumber(result.getStr("blockNumber"));
        if (result.getBool("statusOK")){
            rightDao.saveRight(right);
            System.out.println("addCommonInfoResult ===>" + Result.success(rightCommonInfo.getRightNo()));
//            返回产权编号
            return Result.success(rightCommonInfo.getRightNo());
        } else {
            return Result.failure("添加失败");
        }

    }
    @Override
    public Result<String> fillRightAddress(RightAddress fillRight) {
        List<Object> params = new ArrayList<>();
        params.add(fillRight.getRightNo());
        params.add(fillRight.getZcode());
        params.add(fillRight.getProvince());
        params.add(fillRight.getCity());
        params.add(fillRight.getRegion());
        params.add(fillRight.getTown());
        params.add(fillRight.getVillage());
        params.add(fillRight.getGroup());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"fillRightAddress",params));
        System.out.println("FillRightAddressResult ===>" + result);
        if (result.getBool("statusOK")){
            return Result.success("添加成功");
        } else {
            return Result.failure("添加失败");
        }
    }
    public Result<String> updateRightAddress(RightAddress fillRight) {
        List<Object> params = new ArrayList<>();
        params.add(fillRight.getRightNo());
        params.add(fillRight.getZcode());
        params.add(fillRight.getProvince());
        params.add(fillRight.getCity());
        params.add(fillRight.getRegion());
        params.add(fillRight.getTown());
        params.add(fillRight.getVillage());
        params.add(fillRight.getGroup());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"updateRightAddress",params));
        System.out.println("updateRightAddressResult ===>" + result);
        if (result.getBool("statusOK")){
            return Result.success("更新成功");
        } else {
            return Result.failure("更新失败");
        }
    }

    @Override
    public Result<String> fillFourAddr(FourAddr fourAddr) {
        List<Object> params = new ArrayList<>();
        params.add(fourAddr.getRightNo());
        params.add(fourAddr.getFourEast());
        params.add(fourAddr.getFourWest());
        params.add(fourAddr.getFourSouth());
        params.add(fourAddr.getFourNorth());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"fillFourAddr",params));
        if (result.getBool("statusOK")){
            return Result.success("添加成功");
        } else {
            return Result.failure("添加失败");
        }
    }
    public Result<String> updateFourAddr(FourAddr fourAddr) {
        List<Object> params = new ArrayList<>();
        params.add(fourAddr.getRightNo());
        params.add(fourAddr.getFourEast());
        params.add(fourAddr.getFourWest());
        params.add(fourAddr.getFourSouth());
        params.add(fourAddr.getFourNorth());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"updateFourAddr",params));
        if (result.getBool("statusOK")){
            return Result.success("更新成功");
        } else {
            return Result.failure("更新失败");
        }
    }

    @Override
    public Result<String> fillLandDetails(LandDetail landDetail) {
        List<Object> params = new ArrayList<>();
        params.add(landDetail.getRightNo());
        params.add(landDetail.getLandNature());
        params.add(Long.parseLong(landDetail.getLandArea()));
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"fillLandDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("添加成功");
        } else {
            return Result.failure("添加失败");
        }
    }
    public Result<String> updateLandDetails(LandDetail landDetail) {
        List<Object> params = new ArrayList<>();
        params.add(landDetail.getRightNo());
        params.add(landDetail.getLandNature());
        params.add(Long.parseLong(landDetail.getLandArea()));
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"updateLandDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("更新成功");
        } else {
            return Result.failure("更新失败");
        }
    }

    @Override
    public Result<String> fillFacilityDetails(FacilityDetail facilityDetail) {
        List<Object> params = new ArrayList<>();
        params.add(facilityDetail.getRightNo());
        params.add(facilityDetail.getPlanUse());
        params.add(facilityDetail.getFacilities());
        params.add(facilityDetail.getEnvironment());
        params.add(facilityDetail.getSpecification());
        params.add(Long.parseLong(facilityDetail.getAmount()));
        params.add(facilityDetail.getAmountUnit());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"fillFacilityDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("添加成功");
        } else {
            return Result.failure("添加失败");
        }
    }
    public Result<String> updateFacilityDetails(FacilityDetail facilityDetail) {
        List<Object> params = new ArrayList<>();
        params.add(facilityDetail.getRightNo());
        params.add(facilityDetail.getPlanUse());
        params.add(facilityDetail.getFacilities());
        params.add(facilityDetail.getEnvironment());
        params.add(facilityDetail.getSpecification());
        params.add(Long.parseLong(facilityDetail.getAmount()));
        params.add(facilityDetail.getAmountUnit());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"updateFacilityDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("更新成功");
        } else {
            return Result.failure("更新失败");
        }
    }
    @Override
    public Result<String> fillHouseDetails(HouseDetail houseDetail) {
        List<Object> params = new ArrayList<>();
        params.add(houseDetail.getRightNo());
        params.add(Long.parseLong(houseDetail.getLandNature()));
        params.add(houseDetail.getLandCertNo());
        params.add(houseDetail.getEstateCertNo());
        params.add(houseDetail.getPlanUse());
        params.add(Long.parseLong(houseDetail.getRollOutArea()));
        params.add(Long.parseLong(houseDetail.getLandOutArea()));
        params.add(houseDetail.getSumPeopleName());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"fillHouseDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("添加成功");
        } else {
            return Result.failure("添加失败");
        }

    }
    public Result<String> updateHouseDetails(HouseDetail houseDetail) {
        List<Object> params = new ArrayList<>();
        params.add(houseDetail.getRightNo());
        params.add(Long.parseLong(houseDetail.getLandNature()));
        params.add(houseDetail.getLandCertNo());
        params.add(houseDetail.getEstateCertNo());
        params.add(houseDetail.getPlanUse());
        params.add(Long.parseLong(houseDetail.getRollOutArea()));
        params.add(Long.parseLong(houseDetail.getLandOutArea()));
        params.add(houseDetail.getSumPeopleName());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"updateHouseDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("更新成功");
        } else {
            return Result.failure("更新失败");
        }

    }
    @Override
    public Result<String> fillIPDetails(IPDetail ipDetail) {
        List<Object> params = new ArrayList<>();
        params.add(ipDetail.getRightNo());
        params.add(ipDetail.getPatentNo());
        params.add(ipDetail.getMainProduct());
        params.add(ipDetail.getApprovalNo());
        params.add(Long.parseLong(ipDetail.getApplyDate()));
        params.add(Long.parseLong(ipDetail.getAuthDate()));
        params.add(ipDetail.getTradeRegAddr());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"fillIPDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("添加成功");
        } else {
            return Result.failure("添加失败");
        }

    }
    public Result<String> updateIPDetails(IPDetail ipDetail) {
        List<Object> params = new ArrayList<>();
        params.add(ipDetail.getRightNo());
        params.add(ipDetail.getPatentNo());
        params.add(ipDetail.getMainProduct());
        params.add(ipDetail.getApprovalNo());
        params.add(Long.parseLong(ipDetail.getApplyDate()));
        params.add(Long.parseLong(ipDetail.getAuthDate()));
        params.add(ipDetail.getTradeRegAddr());
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"updateIPDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("更新成功");
        } else {
            return Result.failure("更新失败");
        }

    }
    @Override
    public Result<String> fillForestDetails(ForestDetails forestDetails) {
        List<Object> params = new ArrayList<>();
        params.add(forestDetails.getRightNo());
        params.add(forestDetails.getSmallAddr());
        params.add(forestDetails.getForestClass());
        params.add(forestDetails.getSmallClass());
        params.add(Long.parseLong(forestDetails.getCertArea()));
        params.add(Long.parseLong(forestDetails.getTreeNum()));
        params.add(forestDetails.getMainSeed());
        params.add(forestDetails.getTreeSpecies());
        params.add(Long.parseLong(forestDetails.getOwnerType()));
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"fillForestDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("添加成功");
        } else {
            return Result.failure("添加失败");
        }
    }
    public Result<String> updateForestDetails(ForestDetails forestDetails) {
        List<Object> params = new ArrayList<>();
        params.add(forestDetails.getRightNo());
        params.add(forestDetails.getSmallAddr());
        params.add(forestDetails.getForestClass());
        params.add(forestDetails.getSmallClass());
        params.add(Long.parseLong(forestDetails.getCertArea()));
        params.add(Long.parseLong(forestDetails.getTreeNum()));
        params.add(forestDetails.getMainSeed());
        params.add(forestDetails.getTreeSpecies());
        params.add(Long.parseLong(forestDetails.getOwnerType()));
        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"updateForestDetails",params));
        if (result.getBool("statusOK")){
            return Result.success("更新成功");
        } else {
            return Result.failure("更新失败");
        }
    }
    @Override
    public Result<RightCommonInfo> getRightCommon(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        RightCommonInfo rightCommonInfo = new RightCommonInfo();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getRightCommon",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getRightCommon",response.getStr("output")));
        rightCommonInfo.setRightNo(result.get(0));
        rightCommonInfo.setRightName(result.get(1));
        rightCommonInfo.setOrgId(result.get(2));
        rightCommonInfo.setRightCertCode(result.get(3));
        rightCommonInfo.setRightOwner(result.get(4));
        rightCommonInfo.setUserId(result.get(5));
        rightCommonInfo.setOwnerShip(result.get(6));
        rightCommonInfo.setUseStartDate(result.get(7));
        rightCommonInfo.setUseEndDate(result.get(8));
        rightCommonInfo.setRemark(result.get(9));
        return Result.success(rightCommonInfo);
    }
    public RightCommonInfo getRightCommonEntity(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        RightCommonInfo rightCommonInfo = new RightCommonInfo();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getRightCommon",params));
        JSONArray output = JSONUtil.parseArray(codeUtils.decodeRight("getRightCommon",response.getStr("output")));
        List<String> result = output.toList(String.class);
//        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getRightCommon",response.getStr("output")));
        rightCommonInfo.setRightNo(rightNo);
        rightCommonInfo.setRightName(result.get(1));
        rightCommonInfo.setOrgId(result.get(2));
        rightCommonInfo.setRightCertCode(result.get(3));
        rightCommonInfo.setRightOwner(result.get(4));
        rightCommonInfo.setUserId(result.get(5));
        rightCommonInfo.setOrgName(rightDao.getOrgNameById(rightCommonInfo.getOrgId()));
        rightCommonInfo.setUserName(rightDao.getUserNameById(rightCommonInfo.getUserId()));
        rightCommonInfo.setOwnerShip(result.get(6));
        rightCommonInfo.setUseStartDate(result.get(7));
        rightCommonInfo.setUseEndDate(result.get(8));
        rightCommonInfo.setRemark(result.get(9));
        return rightCommonInfo;
    }

    @Override
    public Result<RightAddress> getRightAddr(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        RightAddress fillRight = new RightAddress();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getRightAddr",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getRightAddr",response.getStr("output")));
        fillRight.setRightNo(result.get(0));
        fillRight.setZcode(result.get(1));
        fillRight.setProvince(result.get(2));
        fillRight.setCity(result.get(3));
        fillRight.setRegion(result.get(4));
        fillRight.setTown(result.get(5));
        fillRight.setVillage(result.get(6));
        fillRight.setGroup(result.get(7));
        return Result.success(fillRight);
    }
    public RightAddress getRightAddrEntity(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        RightAddress fillRight = new RightAddress();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getRightAddr",params));
        List<Object> result = codeUtils.decodeRight("getRightAddr",response.getStr("output"));
//        List<String> result = CodeUtils.convertToStringList(output);

        fillRight.setRightNo(rightNo);
        fillRight.setZcode((String) result.get(0));
        fillRight.setProvince((String) result.get(1));
        fillRight.setCity((String) result.get(2));
        fillRight.setRegion((String) result.get(3));
        fillRight.setTown((String) result.get(4));
        fillRight.setVillage((String) result.get(5));
        fillRight.setGroup((String) result.get(6));
        return fillRight;
    }
    @Override
    public Result<LandDetail> getLandDetails(String rightNo) throws ABICodecException {
        List <Object> params = new ArrayList<>();
        LandDetail landDetail = new LandDetail();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getLandDetails",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getLandDetails",response.getStr("output")));
        landDetail.setRightNo(result.get(0));
        landDetail.setLandNature(result.get(1));
        landDetail.setLandArea(result.get(2));
        return Result.success(landDetail);
    }
    public LandDetail getLandDetailsEntity(String rightNo) throws ABICodecException {
        List <Object> params = new ArrayList<>();
        LandDetail landDetail = new LandDetail();
        params.add(rightNo);
//        System.out.println("getLandDetailsEntityResult ===>" + HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getLandDetails",params));
        JSONArray response = JSONUtil.parseArray(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getLandDetails",params));
//        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getLandDetails",response.getStr("output")));
        List<String> result = response.toList(String.class);
        landDetail.setRightNo(rightNo);
        landDetail.setLandNature(result.get(0));
        landDetail.setLandArea(result.get(1));
        landDetail.setFourEast(result.get(2));
        landDetail.setFourWest(result.get(3));
        landDetail.setFourNorth(result.get(4));
        landDetail.setFourSouth(result.get(5));

        return landDetail;
    }

    @Override
    public Result<ForestDetails> getForestDetails(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        ForestDetails forestDetails = new ForestDetails();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getForestDetails",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getForestDetails",response.getStr("output")));
        forestDetails.setRightNo(result.get(0));
        forestDetails.setSmallAddr(result.get(1));
        forestDetails.setForestClass(result.get(2));
        forestDetails.setSmallClass(result.get(3));
        forestDetails.setCertArea(result.get(4));
        forestDetails.setTreeNum(result.get(5));
        forestDetails.setMainSeed(result.get(6));
        forestDetails.setTreeSpecies(result.get(7));
        forestDetails.setOwnerType(result.get(8));
        return Result.success(forestDetails);
    }
    public ForestDetails getForestDetailsEntity(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        ForestDetails forestDetails = new ForestDetails();
        params.add(rightNo);
//        System.out.println("getForestDetailsEntityResult ===>" + HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getForestDetails",params));
//        JSONArray content = JSONUtil.parseArray(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getForestDetails",params));
//        List<String> result = content.toList(String.class);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getForestDetails",params));
        JSONArray output = JSONUtil.parseArray(codeUtils.decodeRight("getForestDetails",response.getStr("output")));
        List<String> result = output.toList(String.class);
        forestDetails.setRightNo(rightNo);
        forestDetails.setSmallAddr(result.get(0));
        forestDetails.setForestClass(result.get(1));
        forestDetails.setSmallClass(result.get(2));
        forestDetails.setCertArea(result.get(3));
        forestDetails.setTreeNum(result.get(4));
        forestDetails.setMainSeed(result.get(5));
        forestDetails.setTreeSpecies(result.get(6));
        forestDetails.setOwnerType(result.get(7));
        return forestDetails;
    }

    @Override
    public Result<FourAddr> getForestFourAddr(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        FourAddr fourAddr = new FourAddr();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getForestFourAddr",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getForestFourAddr",response.getStr("output")));
        fourAddr.setRightNo(result.get(0));
        fourAddr.setFourEast(result.get(1));
        fourAddr.setFourWest(result.get(2));
        fourAddr.setFourSouth(result.get(3));
        fourAddr.setFourNorth(result.get(4));
        return Result.success(fourAddr);
    }
    public FourAddr getForestFourAddrEntity(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        FourAddr fourAddr = new FourAddr();
        params.add(rightNo);
        JSONArray content = JSONUtil.parseArray(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getForestFourAddr",params));
        List<String> result = content.toList(String.class);
//        JSONObject response = JSONUtil.parseObj(content);
//        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getForestFourAddr",response.getStr("output")));
        fourAddr.setRightNo(rightNo);
        fourAddr.setFourEast(result.get(0));
        fourAddr.setFourWest(result.get(1));
        fourAddr.setFourSouth(result.get(2));
        fourAddr.setFourNorth(result.get(3));
        return fourAddr;
    }

    @Override
    public Result<IPDetail> getIPDetails(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        IPDetail ipDetail = new IPDetail();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getIPDetails",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getIPDetails",response.getStr("output")));
        ipDetail.setRightNo(result.get(0));
        ipDetail.setPatentNo(result.get(1));
        ipDetail.setMainProduct(result.get(2));
        ipDetail.setApprovalNo(result.get(3));
        ipDetail.setApplyDate(result.get(4));
        ipDetail.setAuthDate(result.get(5));
        ipDetail.setTradeRegAddr(result.get(6));
        return Result.success(ipDetail);
    }
    public IPDetail getIPDetailsEntity(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        IPDetail ipDetail = new IPDetail();
        params.add(rightNo);
        JSONArray response = JSONUtil.parseArray(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getIPDetails",params));
        List<String> result = response.toList(String.class);
//        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getIPDetails",response.getStr("output")));
        ipDetail.setRightNo(rightNo);
        ipDetail.setPatentNo(result.get(0));
        ipDetail.setMainProduct(result.get(1));
        ipDetail.setApprovalNo(result.get(2));
        ipDetail.setApplyDate(result.get(3));
        ipDetail.setAuthDate(result.get(4));
        ipDetail.setTradeRegAddr(result.get(5));
        return ipDetail;
    }
    @Override
    public Result<FacilityDetail> getFacilityDetails(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        FacilityDetail facilityDetail = new FacilityDetail();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getFacilityDetails",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getFacilityDetails",response.getStr("output")));
        facilityDetail.setRightNo(result.get(0));
        facilityDetail.setPlanUse(result.get(1));
        facilityDetail.setFacilities(result.get(2));
        facilityDetail.setEnvironment(result.get(3));
        facilityDetail.setSpecification(result.get(4));
        facilityDetail.setAmount(result.get(5));
        facilityDetail.setAmountUnit(result.get(6));
        return Result.success(facilityDetail);
    }
    public FacilityDetail getFacilityDetailsEntity(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        FacilityDetail facilityDetail = new FacilityDetail();
        params.add(rightNo);
        JSONArray response = JSONUtil.parseArray(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getFacilityDetails",params));
//        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getFacilityDetails",response.getStr("output")));
        List<String> result = response.toList(String.class);
        facilityDetail.setRightNo(rightNo);
        facilityDetail.setPlanUse(result.get(0));
        facilityDetail.setFacilities(result.get(1));
        facilityDetail.setEnvironment(result.get(2));
        facilityDetail.setSpecification(result.get(3));
        facilityDetail.setAmount(result.get(4));
        facilityDetail.setAmountUnit(result.get(5));
        return facilityDetail;
    }

    @Override
    public Result<HouseDetail> getHouseDetails(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        HouseDetail houseDetail = new HouseDetail();
        params.add(rightNo);
        JSONObject response = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getHouseDetails",params));
        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getHouseDetails",response.getStr("output")));
        houseDetail.setRightNo(result.get(0));
        houseDetail.setLandNature(result.get(1));
        houseDetail.setLandCertNo(result.get(2));
        houseDetail.setEstateCertNo(result.get(3));
        houseDetail.setPlanUse(result.get(4));
        houseDetail.setRollOutArea(result.get(5));
        houseDetail.setLandOutArea(result.get(6));
        houseDetail.setSumPeopleName(result.get(7));
        return Result.success(houseDetail);
    }
    public HouseDetail getHouseDetailsEntity(String rightNo) throws ABICodecException {
        List<Object> params = new ArrayList<>();
        HouseDetail houseDetail = new HouseDetail();
        params.add(rightNo);
        JSONArray content = JSONUtil.parseArray(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"getHouseDetails",params));
//        JSONObject response = JSONUtil.parseObj(content);
//        List<String> result = CodeUtils.convertToStringList(codeUtils.decodeRight("getHouseDetails",response.getStr("output")));
        List<String> result = content.toList(String.class);
        houseDetail.setRightNo(rightNo);
        houseDetail.setLandNature(result.get(0));
        houseDetail.setLandCertNo(result.get(1));
        houseDetail.setEstateCertNo(result.get(2));
        houseDetail.setPlanUse(result.get(3));
        houseDetail.setRollOutArea(result.get(4));
        houseDetail.setLandOutArea(result.get(5));
        houseDetail.setSumPeopleName(result.get(6));
        return houseDetail;
    }

    @Override
    public Result<String> addHouseRight(HouseAll houseAll) {
        JSONObject result = JSONUtil.parseObj(addCommonInfo(houseAll.getRightCommonInfo()));
        String rightNo = result.getStr("data");
        RightAddress rightAddress = houseAll.getRightAddress();
        HouseDetail houseDetail = houseAll.getHouseDetail();
        // 设置RightNo值
        rightAddress.setRightNo(rightNo);
        houseDetail.setRightNo(rightNo);
        // 调用方法
        fillRightAddress(houseAll.getRightAddress());
        fillHouseDetails(houseAll.getHouseDetail());
        return Result.success("添加成功");
    }

    @Override
    public Result<String> addForeRight(ForeAll foreAll) {
        JSONObject result = JSONUtil.parseObj(addCommonInfo(foreAll.getRightCommonInfo()));
        String rightNo = result.getStr("data");
        FourAddr fourAddr = foreAll.getFourAddr();
        RightAddress rightAddress = foreAll.getRightAddress();
        ForestDetails forestDetails = foreAll.getForestDetails();
        fourAddr.setRightNo(rightNo);
        rightAddress.setRightNo(rightNo);
        forestDetails.setRightNo(rightNo);
        fillFourAddr(fourAddr);
        fillRightAddress(rightAddress);
        fillForestDetails(forestDetails);
        return Result.success("添加成功");
    }

    @Override
    public Result<String> addLandRight(LandAll landAll) {
//        公共信息初始化上链并返回产权编号
        JSONObject result = JSONUtil.parseObj(addCommonInfo(landAll.getRightCommonInfo()));
        String rightNo = result.getStr("data");
        FourAddr fourAddr = landAll.getFourAddr();
        RightAddress rightAddress = landAll.getRightAddress();
        LandDetail landDetail = landAll.getLandDetail();
        // 设置RightNo值,要通过产权编号进行数据上链
        fourAddr.setRightNo(rightNo);
        rightAddress.setRightNo(rightNo);
        landDetail.setRightNo(rightNo);
        // 调用方法
//        填充产权四至信息
        fillFourAddr(fourAddr);
//        填充产权座落信息
        fillRightAddress(rightAddress);
//        填充产权专有信息
        fillLandDetails(landDetail);
        return Result.success("添加成功");
    }

    @Override
    public Result<String> addIPRight(IPAll ipAll) {
        JSONObject result = JSONUtil.parseObj(addCommonInfo(ipAll.getRightCommonInfo()));
        String rightNo = result.getStr("data");
        RightAddress rightAddress = ipAll.getRightAddress();
        IPDetail ipDetail = ipAll.getIpDetail();
        rightAddress.setRightNo(rightNo);
        ipDetail.setRightNo(rightNo);
        fillRightAddress(rightAddress);
        fillIPDetails(ipDetail);
        return Result.success("添加成功");
    }

    @Override
    public Result<String> addFacilityRight(FacilityAll facilityAll) {
        JSONObject result = JSONUtil.parseObj(addCommonInfo(facilityAll.getRightCommonInfo()));
        String rightNo = result.getStr("data");
        RightAddress rightAddress = facilityAll.getRightAddress();
        FacilityDetail facilityDetail = facilityAll.getFacilityDetail();
        // 设置RightNo值
        rightAddress.setRightNo(rightNo);
        facilityDetail.setRightNo(rightNo);
        // 调用方法
        fillRightAddress(rightAddress);
        fillFacilityDetails(facilityDetail);
        return Result.success("添加成功");
    }

    @Override
    public Result<List<Object>> getOrgTypeRight(QueryTypeRightRequest queryTypeRightRequest) throws ABICodecException {
        List<String> codeList = rightDao.getTypeRightCodeList(queryTypeRightRequest.getTransKind(), queryTypeRightRequest.getOrgId());
        List<Object> result = new ArrayList<>();
        switch (queryTypeRightRequest.getTransKind()){
            case "A":{
                for (String code : codeList){
                    JSONObject landInfo = new JSONObject();

                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    String orgName = rightDao.getOrgNameById(rightCommonInfo.getOrgId());
                    rightCommonInfo.setOrgName(orgName);

                    LandDetail landDetail = getLandDetailsEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    landInfo.set("rightCommonInfo",rightCommonInfo);
                    landInfo.set("landDetail",landDetail);
                    landInfo.set("rightAddress",rightAddress);
                    result.add(landInfo);

                }
                return Result.success(result);
            }
            case "B":{
                for (String code : codeList){
                    JSONObject forestInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    FourAddr fourAddr = getForestFourAddrEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    ForestDetails forestDetails = getForestDetailsEntity(code);
                    forestInfo.set("rightCommonInfo",rightCommonInfo);
                    forestInfo.set("fourAddr",fourAddr);
                    forestInfo.set("forestDetails",forestDetails);
                    forestInfo.set("rightAddress",rightAddress);
                    result.add(forestInfo);

                }
                return Result.success(result);
            }
            case "C":{
                for (String code : codeList){
                    JSONObject houseInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    HouseDetail houseDetail = getHouseDetailsEntity(code);
                    houseInfo.set("rightCommonInfo",rightCommonInfo);
                    houseInfo.set("houseDetail",houseDetail);
                    houseInfo.set("rightAddress",rightAddress);
                    result.add(houseInfo);

                }
                return Result.success(result);
            }
            case "D":{
                for (String code : codeList){
                    JSONObject facilityInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    FacilityDetail facilityDetail = getFacilityDetailsEntity(code);
                    facilityInfo.set("rightCommonInfo",rightCommonInfo);
                    facilityInfo.set("houseDetail",facilityDetail);
                    facilityInfo.set("rightAddress",rightAddress);
                    result.add(facilityInfo);

                }
                return Result.success(result);
            }
            case "E":{
                for (String code : codeList){
                    JSONObject ipInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    IPDetail ipDetail = getIPDetailsEntity(code);
                    ipInfo.set("rightCommonInfo",rightCommonInfo);
                    ipInfo.set("ipDetail",ipDetail);
                    ipInfo.set("rightAddress",rightAddress);
                    result.add(ipInfo);

               }
                return Result.success(result);
            }
        }
        return Result.failure("没有找到匹配的产权类型");
    }

    @Override
    public Result<List<Object>> getTypeRight(String transKind) throws ABICodecException {
//        List<String> codeList = rightDao.getTypeRightCodeList(queryTypeRightRequest.getTransKind(), queryTypeRightRequest.getOrgId());
        List<String> codeList = rightDao.getTransKindRightCodeList(transKind);
        List<Object> rightList = new ArrayList<>();
        switch (transKind){
            case "A":{
                for (String code : codeList){
                    JSONObject landInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    LandDetail landDetail = getLandDetailsEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    landInfo.set("rightCommonInfo",rightCommonInfo);
                    landInfo.set("landDetail",landDetail);
                    landInfo.set("rightAddress",rightAddress);
                    rightList.add(landInfo);
                }
                return Result.success(rightList);
            }
            case "B":{
                for (String code : codeList){
                    JSONObject forestInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    FourAddr fourAddr = getForestFourAddrEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    ForestDetails forestDetails = getForestDetailsEntity(code);
                    forestInfo.set("rightCommonInfo",rightCommonInfo);
                    forestInfo.set("fourAddr",fourAddr);
                    forestInfo.set("forestDetails",forestDetails);
                    forestInfo.set("rightAddress",rightAddress);
                    rightList.add(forestInfo);
                }
                return Result.success(rightList);
            }
            case "C":{
                for (String code : codeList){
                    JSONObject houseInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    HouseDetail houseDetail = getHouseDetailsEntity(code);
                    houseInfo.set("rightCommonInfo",rightCommonInfo);
                    houseInfo.set("houseDetail",houseDetail);
                    houseInfo.set("rightAddress",rightAddress);
                    rightList.add(houseInfo);
                }
                return Result.success(rightList);
            }
            case "D":{
                for (String code : codeList){
                    JSONObject facilityInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    FacilityDetail facilityDetail = getFacilityDetailsEntity(code);
                    facilityInfo.set("rightCommonInfo",rightCommonInfo);
                    facilityInfo.set("houseDetail",facilityDetail);
                    facilityInfo.set("rightAddress",rightAddress);
                    rightList.add(facilityInfo);
                }
                return Result.success(rightList);
            }
            case "E":{
                for (String code : codeList){
                    JSONObject ipInfo = new JSONObject();
                    RightCommonInfo rightCommonInfo = getRightCommonEntity(code);
                    RightAddress rightAddress = getRightAddrEntity(code);
                    IPDetail ipDetail = getIPDetailsEntity(code);
                    ipInfo.set("rightCommonInfo",rightCommonInfo);
                    ipInfo.set("ipDetail",ipDetail);
                    ipInfo.set("rightAddress",rightAddress);
                    rightList.add(ipInfo);
                }
                return Result.success(rightList);
            }
        }
        return Result.failure("没有找到匹配的产权类型");
    }

    @Override
    public Result<Object> getRightById(String rightNo) throws ABICodecException {
//        List<String> codeList = rightDao.getTransKindRightCodeList(rightNo);
        String rightType = rightDao.getTypeByNo(rightNo);
//        List<Object> rightList = new ArrayList<>();
        switch (rightType){
            case "A":{
                JSONObject landInfo = new JSONObject();
                RightCommonInfo rightCommonInfo = getRightCommonEntity(rightNo);
                LandDetail landDetail = getLandDetailsEntity(rightNo);
                RightAddress rightAddress = getRightAddrEntity(rightNo);
                landInfo.set("rightCommonInfo",rightCommonInfo);
                landInfo.set("landDetail",landDetail);
                landInfo.set("rightAddress",rightAddress);
//                rightList.add(landInfo);
                return Result.success(landInfo);
            }
            case "B":{
                JSONObject forestInfo = new JSONObject();
                RightCommonInfo rightCommonInfo = getRightCommonEntity(rightNo);
                FourAddr fourAddr = getForestFourAddrEntity(rightNo);
                RightAddress rightAddress = getRightAddrEntity(rightNo);
                ForestDetails forestDetails = getForestDetailsEntity(rightNo);
                forestInfo.set("rightCommonInfo",rightCommonInfo);
                forestInfo.set("fourAddr",fourAddr);
                forestInfo.set("forestDetails",forestDetails);
                forestInfo.set("rightAddress",rightAddress);
//                rightList.add(forestInfo);
                return Result.success(forestInfo);

            }
            case "C":{
                JSONObject houseInfo = new JSONObject();
                RightCommonInfo rightCommonInfo = getRightCommonEntity(rightNo);
                RightAddress rightAddress = getRightAddrEntity(rightNo);
                HouseDetail houseDetail = getHouseDetailsEntity(rightNo);
                houseInfo.set("rightCommonInfo",rightCommonInfo);
                houseInfo.set("houseDetail",houseDetail);
                houseInfo.set("rightAddress",rightAddress);
//                rightList.add(houseInfo);
                return Result.success(houseInfo);

            }
            case "D":{
                JSONObject facilityInfo = new JSONObject();
                RightCommonInfo rightCommonInfo = getRightCommonEntity(rightNo);
                RightAddress rightAddress = getRightAddrEntity(rightNo);
                FacilityDetail facilityDetail = getFacilityDetailsEntity(rightNo);
                facilityInfo.set("rightCommonInfo",rightCommonInfo);
                facilityInfo.set("houseDetail",facilityDetail);
                facilityInfo.set("rightAddress",rightAddress);
//                rightList.add(facilityInfo);
                return Result.success(facilityInfo);

            }
            case "E":{
                JSONObject ipInfo = new JSONObject();
                RightCommonInfo rightCommonInfo = getRightCommonEntity(rightNo);
                RightAddress rightAddress = getRightAddrEntity(rightNo);
                IPDetail ipDetail = getIPDetailsEntity(rightNo);
                ipInfo.set("rightCommonInfo",rightCommonInfo);
                ipInfo.set("ipDetail",ipDetail);
                ipInfo.set("rightAddress",rightAddress);
//                rightList.add(ipInfo);
                return Result.success(ipInfo);
            }
        }
        return Result.failure("没有找到匹配的产权类型");
    }





    @Override
    public Result<String> updateCommonInfo(RightCommonInfo rightCommonInfo) {
        List<Object> params = new ArrayList<>();
        rightCommonInfo.setRightNo(noUtils.generateNo("CQ", rightCommonInfo.getType()));
        params.add(rightCommonInfo.getRightNo());
        params.add(rightCommonInfo.getRightName());
        params.add(Long.parseLong(rightCommonInfo.getOrgId()));
        params.add(rightCommonInfo.getRightCertCode());
        params.add(rightCommonInfo.getRightOwner());
        params.add(Long.parseLong(rightCommonInfo.getUserId()));
        params.add(Long.parseLong(rightCommonInfo.getOwnerShip()));
        params.add(rightCommonInfo.getUseStartDate());
        params.add(rightCommonInfo.getUseEndDate());
        params.add(rightCommonInfo.getRemark());
        Right right = new Right();
        right.setRightCertCode(rightCommonInfo.getRightCertCode());
        right.setRightNo(rightCommonInfo.getRightNo());
        right.setOrganId(rightCommonInfo.getOrgId());
        right.setCreateTime(TimeUtils.getCurrentDateTime());
        right.setEditTime(TimeUtils.getCurrentDateTime());
        right.setRightName(rightCommonInfo.getRightName());
        right.setTransKind(rightCommonInfo.getType());
        right.setRightOwner(rightCommonInfo.getRightOwner());

        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"updateCommonInfo",params));
        right.setBlockNumber(result.getStr("blockNumber"));
        if (result.getBool("statusOK")){
            rightDao.saveRight(right);
            System.out.println("updateCommonInfoResult ===>" + Result.success(rightCommonInfo.getRightNo()));
            return Result.success(rightCommonInfo.getRightNo());
        } else {
            return Result.failure("添加失败");
        }

    }

//    @Override
//    public Result<Object> goUpInfo(String rightNo,String userId) {
//        List<Object> params = new ArrayList<>();
//        params.add(rightNo);
//        params.add(Long.parseLong(userId));
//        JSONObject result = JSONUtil.parseObj(HttpUtils.rightRequest(EnvConfig.ADMIN_ADDRESS,"goUpInfo",params));
//        if (result.getBool("statusOK")){
//                rightDao.updateIsPubUp()
//            return Result.success("发布挂牌成功");
//        } else {
//            return Result.failure("发布挂牌失败");
//        }
//    }

    @Override
    public Result<Boolean> syncRight() {
        HouseAll houseAll = new HouseAll();
        RightCommonInfo rightCommonInfo = new RightCommonInfo();
        rightCommonInfo.setRightNo(noUtils.generateNo("CQ", rightCommonInfo.getType()));
        rightCommonInfo.setRightName("测试");
        rightCommonInfo.setOrgId(noUtils.generateNo("ORG", rightCommonInfo.getType()));
        rightCommonInfo.setRightCertCode(noUtils.generateNo("CERT", rightCommonInfo.getType()));
        rightCommonInfo.setRightOwner(noUtils.generateNo("USER", rightCommonInfo.getType()));
        rightCommonInfo.setUserId(noUtils.generateNo("USER", rightCommonInfo.getType()));
        rightCommonInfo.setOwnerShip(noUtils.generateNo("USER", rightCommonInfo.getType()));
        rightCommonInfo.setUseStartDate(TimeUtils.getCurrentDateTime());
        rightCommonInfo.setUseEndDate(TimeUtils.getCurrentDateTime());
        rightCommonInfo.setRemark("测试");
        rightCommonInfo.setType("H");
        rightCommonInfo.setOrgName("测试");
        rightCommonInfo.setUserName("测试");
        houseAll.setRightCommonInfo(rightCommonInfo);
        RightAddress rightAddress = new RightAddress();
        rightAddress.setRightNo(rightCommonInfo.getRightNo());
        rightAddress.setZcode("110101");
        rightAddress.setProvince("北京市");
        rightAddress.setCity("北京市");
        rightAddress.setRegion("东城区");
        rightAddress.setTown("王府井");
        rightAddress.setVillage("王府井");
        rightAddress.setGroup("王府井");
        houseAll.setRightAddress(rightAddress);
        HouseDetail houseDetail = new HouseDetail();
        houseDetail.setRightNo(rightCommonInfo.getRightNo());
        houseDetail.setLandNature("普通");
        houseDetail.setLandCertNo("123456789");
        houseDetail.setEstateCertNo("123456789");
        houseDetail.setPlanUse("普通住宅");
        houseDetail.setRollOutArea("100");
        houseDetail.setLandOutArea("100");
        houseDetail.setSumPeopleName("王小明");
        houseAll.setHouseDetail(houseDetail);
        addHouseRight(houseAll);
        return Result.success(true);

    }

    @Override
    public Result<Object> goSuPu(String projectCode, String userId) {
        List<Object> params =  new ArrayList<>();
        params.add(projectCode);
        params.add(Long.parseLong(userId));
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"goSuPu",params));
        if (result.getBool("statusOK")){
            rightDao.setPubUpSuccess(projectCode);
            return Result.success("上牌成功");
        } else {
            return Result.failure("上牌失败");
        }
    }

//    @Override
//    public Result<String> getOrganAllRightInfo(String organId) {
//        List<String> rightList = rightDao.getOrganAllRightId(organId);
//    }
}
