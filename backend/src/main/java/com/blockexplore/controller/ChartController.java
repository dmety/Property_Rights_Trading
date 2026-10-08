package com.blockexplore.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.model.MoneyAreaDTO;
import com.blockexplore.service.ChartService;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/*
* 数据大屏数据
* */
@RestController
@CrossOrigin
@RequestMapping("/chart")
public class ChartController {
    @Autowired
    private ChartService chartService;
    @RequestMapping("/pie")
    public Result<Object> pie() throws ABICodecException {
        return chartService.getPieData();
    }


//    不同种类平均交易金额
    @RequestMapping("/bar2")
    public Result<List<Object>> getTradeAmount() {
        return Result.success(chartService.getTradeAmount()); // 注意这里没有多余的逗号
    }

//    价格与面积关系散点图
    @RequestMapping("/point2")
    public Result<Object> point2() {
        // 创建一个 List<List<Integer>> 用于存储点数据
        List<List<Integer>> pointData = chartService.getAllMoneyArea();
        // 返回结果
        return Result.success(pointData);
    }

//    不同产权价格分布表
    @RequestMapping("/radar2")
    public Result<Object> radar2() {
        ArrayList<MoneyAreaDTO> data = chartService.getRadar();
        System.out.println("radar2===>"+data);
        List<Integer> radarData = new ArrayList<>();
        for (MoneyAreaDTO dto : data){
            System.out.println("价格====>"+dto.getSumSuccessPrice());
            if (dto.getSumSuccessPrice() <= 0){
                radarData.add(0);
            } else {
                radarData.add(dto.getSumSuccessPrice());
            }
        }
//        4300, 10000, 28000, 35000, 50000
        return Result.success(radarData);
    }

//    获取成交率
@RequestMapping("/dashboard2")
    public Result<Object> dashboard2() throws ABICodecException {
    JSONArray data = chartService.getDashboard();;
        return Result.success(data);
    }

//    获取所有的总数据
    @RequestMapping("/data2")
    public Result<Object> getAllData(){
        MoneyAreaDTO data = chartService.getAllData();
        JSONObject result = new JSONObject();
        result.put("transactionQuantity",data.getCount());
        result.put("circulationArea",data.getArea());
        result.put("transactionAmount",data.getSumSuccessPrice());
        return Result.success(result);
    }



/**
 * 老方法死数据
 * */
    //    @RequestMapping("/pie")
//    public Result<Object> pie2() throws ABICodecException {
//        return Result.success(JSONUtil.parseArray("[\n" +
//                "          { value: 1048, name: '土地' },\n" +
//                "          { value: 735, name: '房屋' },\n" +
//                "          { value: 580, name: '知识产权' },\n" +
//                "          { value: 484, name: '生产设施' },\n" +
//                "          { value: 300, name: '林地' }\n" +
//                "        ]"));
//    }


//    @RequestMapping("/bar2")
//    public Result<Object> bar2() {
//        return Result.success(JSONUtil.parseArray("[\n" +
//                "  { \"value\": 5000, \"itemStyle\": { \"color\": \"#5470c6\" } }," +
//                "  { \"value\": 12000, \"itemStyle\": { \"color\": \"#91cc75\" } }," +
//                "  { \"value\": 8000, \"itemStyle\": { \"color\": \"#fac858\" } }," +
//                "  { \"value\": 6000, \"itemStyle\": { \"color\": \"#ee6666\" } }," +
//                "  { \"value\": 4000, \"itemStyle\": { \"color\": \"#73c0de\" } }" +
//                "]")); // 注意这里没有多余的逗号
//    }

    //    @RequestMapping("/point2")
//    public Result<Object> point2() {
//        // 创建一个 List<List<Integer>> 用于存储点数据
//        List<List<Integer>> pointData = new ArrayList<>();
//
//        // 添加数据点
//        pointData.add(Arrays.asList(10, 100));
//        pointData.add(Arrays.asList(20, 150));
//        pointData.add(Arrays.asList(30, 200));
//        pointData.add(Arrays.asList(40, 300));
//        pointData.add(Arrays.asList(50, 400));
//        pointData.add(Arrays.asList(114, 30));
//
//        // 返回结果
//        return Result.success(pointData);
//    }

    //    @RequestMapping("/dashboard2")
//    public Result<Object> dashboard2() throws ABICodecException {
//        List<Object> result = new ArrayList<>();
//        JSONArray data = JSONUtil.parseArray("[\n" +
//                "            {\n" +
//                "              value: 0.89,\n" +
//                "              name: '成交率'\n" +
//                "            }\n" +
//                "          ]");
//        return Result.success(data);
//    }

    //    @RequestMapping("/data2")
//    public Result<Object> data2(){
//        JSONObject result = JSONUtil.parseObj("{\n" +
//                "    \"transactionQuantity\": 1200,\n" +
//                "    \"circulationArea\": 6530,\n" +
//                "    \"transactionAmount\": 3579\n" +
//                "}\n");
//        return Result.success(result);
//    }

    //    不同产权类型的价格和面积分布图
//    @RequestMapping("/radar2")
//    public Result<Object> radar2() {
//        List<Integer> radarData = new ArrayList<>();
//    //        4300, 10000, 28000, 35000, 50000
//        radarData.add(4300);
//        radarData.add(10000);
//        radarData.add(28000);
//        radarData.add(35000);
//        radarData.add(50000);
//        return Result.success(radarData);
//    }


}
