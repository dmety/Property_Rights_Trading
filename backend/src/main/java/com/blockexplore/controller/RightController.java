package com.blockexplore.controller;

import com.blockexplore.model.*;
import com.blockexplore.request.QueryTypeRightRequest;
import com.blockexplore.service.RightService;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产权控制器类
 * 处理与产权相关的所有REST API请求
 * 所有方法成功返回码：200，失败返回码：500
 */
@RestController
@CrossOrigin
@RequestMapping("/right")
public class RightController {
    @Autowired
    private RightService rightService;

    /**
     * 添加公共信息
     * @param rightCommonInfo 包含公共信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping(value = "/addCommonInfo")
    public Result<String> addCommonInfo(@RequestBody RightCommonInfo rightCommonInfo) {
        return rightService.addCommonInfo(rightCommonInfo);
    }

    /**
     * 填充产权地址信息
     * @param fillRight 包含产权地址信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping(value = "/fillRightAddress")
    public Result<String> addRightInfo(@RequestBody RightAddress fillRight){
        return rightService.fillRightAddress(fillRight);
    }

    /**
     * 填充四至地址信息
     * @param fourAddr 包含四至地址信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping(value = "/fillFourAddr")
    public Result<String> addFourAddr(@RequestBody FourAddr fourAddr){
        return rightService.fillFourAddr(fourAddr);
    }

    /**
     * 填充土地详细信息
     * @param landDetail 包含土地详细信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/fillLandDetails")
    public Result<String> fillLandDetails(@RequestBody LandDetail landDetail){
        return rightService.fillLandDetails(landDetail);
    }

    /**
     * 填充设施详细信息
     * @param facilityDetail 包含设施详细信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/fillFacilityDetails")
    public Result<String> fillFacilityDetails(@RequestBody FacilityDetail facilityDetail){
        return rightService.fillFacilityDetails(facilityDetail);
    }

    /**
     * 填充房屋详细信息
     * @param houseDetail 包含房屋详细信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/fillHouseDetails")
    public Result<String> fillHouseDetails(@RequestBody HouseDetail houseDetail){
        return rightService.fillHouseDetails(houseDetail);
    }

    /**
     * 填充知识产权详细信息
     * @param ipDetail 包含知识产权详细信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/fillIPDetails")
    public Result<String> fillIPDetails(@RequestBody IPDetail ipDetail){
        return rightService.fillIPDetails(ipDetail);
    }

    /**
     * 填充林地详细信息
     * @param forestDetails 包含林地详细信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/fillForestDetails")
    public Result<String> fillForestDetails(ForestDetails forestDetails){
        return rightService.fillForestDetails(forestDetails);
    }

    /**
     * 获取产权公共信息
     * @param rightNo 产权编号
     * @return 操作结果，包含产权公共信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getRightCommon")
    public Result<RightCommonInfo> getRightCommon(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getRightCommon(rightNo);
    }

    /**
     * 获取产权地址信息
     * @param rightNo 产权编号
     * @return 操作结果，包含产权地址信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getRightAddr")
    public Result<RightAddress> getRightAddr(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getRightAddr(rightNo);
    }

    /**
     * 获取土地详细信息
     * @param rightNo 产权编号
     * @return 操作结果，包含土地详细信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getLandDetails")
    public Result<LandDetail> getLandDetails(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getLandDetails(rightNo);
    }

    /**
     * 获取林地详细信息
     * @param rightNo 产权编号
     * @return 操作结果，包含林地详细信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getForestDetails")
    public Result<ForestDetails> getForestDetails(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getForestDetails(rightNo);
    }

    /**
     * 获取林地四至地址信息
     * @param rightNo 产权编号
     * @return 操作结果，包含四至地址信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getForestFourAddr")
    public Result<FourAddr> getForestFourAddr(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getForestFourAddr(rightNo);
    }

    /**
     * 获取知识产权详细信息
     * @param rightNo 产权编号
     * @return 操作结果，包含知识产权详细信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getIPDetails")
    public Result<IPDetail> getIPDetails(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getIPDetails(rightNo);
    }

    /**
     * 获取设施详细信息
     * @param rightNo 产权编号
     * @return 操作结果，包含设施详细信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getFacilityDetails")
    public Result<FacilityDetail> getFacilityDetails(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getFacilityDetails(rightNo);
    }

    /**
     * 获取房屋详细信息
     * @param rightNo 产权编号
     * @return 操作结果，包含房屋详细信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getHouseDetails")
    public Result<HouseDetail> getHouseDetails(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getHouseDetails(rightNo);
    }

    /**
     * 添加房屋产权
     * @param houseAll 包含房屋产权信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/addHouseRight")
    public Result<String> addHouseRight(@RequestBody HouseAll houseAll){
        return rightService.addHouseRight(houseAll);
    }

    /**
     * 添加林地产权
     * @param foreAll 包含林地产权信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/addForeRight")
    public Result<String> addForeRight(@RequestBody ForeAll foreAll){
        return rightService.addForeRight(foreAll);
    }

    /**
     * 添加土地产权
     * @param landAll 包含土地产权信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/addLandRight")
    public Result<String> addLandRight(@RequestBody LandAll landAll){
        return rightService.addLandRight(landAll);
    }

    /**
     * 添加知识产权
     * @param ipAll 包含知识产权信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/addIpRight")
    public Result<String> addIpRight(@RequestBody IPAll ipAll){
        return rightService.addIPRight(ipAll);
    }

    /**
     * 添加设施产权
     * @param facilityAll 包含设施产权信息的对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/addFacilityRight")
    public Result<String> addFacilityRight(@RequestBody FacilityAll facilityAll){
        return rightService.addFacilityRight(facilityAll);
    }

    /**
     * 根据组织类型获取产权列表
     * @param queryTypeRightRequest 包含查询条件的请求对象
     * @return 操作结果，包含产权列表，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @RequestMapping("/getOrgTypeRight")
    public Result<List<Object>> getOrgTypeRight(@RequestBody QueryTypeRightRequest queryTypeRightRequest) throws ABICodecException {
        return rightService.getOrgTypeRight(queryTypeRightRequest);
    }

    /**
     * 根据类型获取产权列表
     * @param transKind 交易类型
     * @return 操作结果，包含产权列表，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @GetMapping("/getTypeRight")
    public Result<List<Object>> getTypeRight(@RequestParam String transKind) throws ABICodecException {
        return rightService.getTypeRight(transKind);
    }

    /**
     * 根据ID获取产权信息
     * @param rightNo 产权编号
     * @return 操作结果，包含产权信息，成功码200，失败码500
     * @throws ABICodecException ABI编解码异常
     */
    @GetMapping("/getRightById")
    public Result<Object> getRightById(@RequestParam("rightNo") String rightNo) throws ABICodecException {
        return rightService.getRightById(rightNo);
    }

    /**
     * 更新公共信息
     * @param rightCommonInfo 包含更新后的公共信息对象
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping(value = "/updateCommonInfo")
    public Result<String> updateCommonInfo(@RequestBody RightCommonInfo rightCommonInfo) {
        return rightService.updateCommonInfo(rightCommonInfo);
    }

    /**
     * 同步产权信息
     * @return 操作结果，包含同步状态，成功码200，失败码500
     */
    @RequestMapping("/syncRight")
    public Result<Boolean> syncRight(){
        return rightService.syncRight();
    }

    /**
     * 挂牌操作
     * @param projectCode 项目代码
     * @param userId 用户ID
     * @return 操作结果，成功码200，失败码500
     */
    @RequestMapping("/goSuPu")
    public Result<Object> goSuPu(@RequestParam("projectCode") String projectCode, @RequestParam("userId") String userId){
        return rightService.goSuPu(projectCode, userId);
    }
}
