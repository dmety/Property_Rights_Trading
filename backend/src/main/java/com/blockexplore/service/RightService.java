package com.blockexplore.service;

import com.blockexplore.model.*;
import com.blockexplore.request.QueryTypeRightRequest;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RightService {
    Result<String> addCommonInfo(RightCommonInfo rightCommonInfo);

    Result<String> fillRightAddress(RightAddress fillRight);

    Result<String> fillFourAddr(FourAddr fourAddr);

    Result<String> fillLandDetails(LandDetail landDetail);

    Result<String> fillFacilityDetails(FacilityDetail facilityDetail);

    Result<String> fillHouseDetails(HouseDetail houseDetail);

    Result<String> fillIPDetails(IPDetail ipDetail);

    Result<String> fillForestDetails(ForestDetails forestDetails);

    Result<RightCommonInfo> getRightCommon(String rightNo) throws ABICodecException;

    Result<RightAddress> getRightAddr(String rightNo) throws ABICodecException;

    Result<LandDetail> getLandDetails(String rightNo) throws ABICodecException;

    Result<ForestDetails> getForestDetails(String rightNo) throws ABICodecException;

    Result<FourAddr> getForestFourAddr(String rightNo) throws ABICodecException;

    Result<IPDetail> getIPDetails(String rightNo) throws ABICodecException;

    Result<FacilityDetail> getFacilityDetails(String rightNo) throws ABICodecException;

    Result<HouseDetail> getHouseDetails(String rightNo) throws ABICodecException;

    Result<String> addHouseRight(HouseAll houseAll);

    Result<String> addForeRight(ForeAll foreAll);

    Result<String> addLandRight(LandAll landAll);

    Result<String> addIPRight(IPAll ipAll);

    Result<String> addFacilityRight(FacilityAll facilityAll);

    Result<List<Object>> getOrgTypeRight(QueryTypeRightRequest queryTypeRightRequest) throws ABICodecException;

    Result<List<Object>> getTypeRight(String transKind) throws ABICodecException;

    Result<Object> getRightById(String rightNo) throws ABICodecException;



//    Result<String> delistProject(String projectCode,String userId);

    Result<String> updateCommonInfo(RightCommonInfo rightCommonInfo);

//    Result<Object> goUpInfo(String rightNo,String userId);

    Result<Boolean> syncRight();

    Result<Object> goSuPu(String projectCode, String userId);

//    Result<String> getOrganAllRightInfo(String organId);
}
