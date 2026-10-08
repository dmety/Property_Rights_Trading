package com.blockexplore.service.impl;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.blockexplore.mapper.ChartMapper;
import com.blockexplore.model.CountMod;
import com.blockexplore.model.MoneyAreaDTO;
import com.blockexplore.service.ChartService;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChartServiceImpl implements ChartService {
    @Autowired
    private ChartMapper chartMapper;

    @Override
    public Result<Object> getPieData() throws ABICodecException {
        // 获取ABCDE五种类型的计数
        List<Integer> counts = chartMapper.getPieData();

        // 定义ABCDE分别对应的名称
        String[] names = { "土地", "房屋", "知识产权", "生产设施", "林地" };

        // 构造返回的结果
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = 0; i < counts.size(); i++) {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("value", counts.get(i));  // 对应的计数
            dataMap.put("name", names[i]);        // 对应的名称
            resultList.add(dataMap);
        }
        // 返回结果，使用自定义的 Result 封装
        return Result.success(resultList);
    }

    @Override
    public List<Object> getTradeAmount() {
        String[] COLORS = {"#5470c6", "#91cc75", "#fac858", "#ee6666", "#73c0de"};

        List<Integer> countsMoney = chartMapper.getTradeAmount();

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("value", i < countsMoney.size() ? countsMoney.get(i) : 0);

            Map<String, String> style = new LinkedHashMap<>();
            style.put("color", COLORS[i]);
            item.put("itemStyle", style);

            dataList.add(item);
        }
        System.out.println(dataList);

        // 直接返回dataList（对应前端需要的数组结构）
        return new ArrayList<>(dataList);
        // 或者用Collections.unmodifiableList(dataList)保持不可变性
    }

//    @Override
//    public List<List<Integer>> getAllMoneyArea() {
//        return chartMapper.getAllMoneyArea().stream()
//                .map(row -> {
//                    Integer amount = Integer.parseInt(row.get("amount").toString());
//                    Integer areaCode = Integer.parseInt(row.get("area_code").toString());
//                    return Arrays.asList(areaCode,amount);
//                })
//                .collect(Collectors.toList());
//    }

    @Override
    public List<List<Integer>> getAllMoneyArea() {
        List<MoneyAreaDTO> allMoneyArea = chartMapper.getAllMoneyArea();
        ArrayList<List<Integer>> result = new ArrayList<>();
        System.out.println(allMoneyArea);
        for (MoneyAreaDTO moneyAreaDTO : allMoneyArea) {
            result.add(Arrays.asList(moneyAreaDTO.getArea(),moneyAreaDTO.getSumSuccessPrice()));
        }
        return result;
    }

    @Override
    public JSONArray getDashboard() {  // 建议返回 JSONObject 而不是 ArrayList<JSONArray>
        CountMod data = chartMapper.getDashboard();
        System.out.println(data);
        // 计算成交率（保留 2 位小数）
        Double rate = data.getVerifiedCount() *1.0 / data.getTotalCount();
        System.out.println(rate);
        // 构造 data 数组
        JSONArray dataArray = new JSONArray();
        JSONObject rateObj = new JSONObject();
        rateObj.put("value", rate);
        rateObj.put("name", "成交率");
        dataArray.add(rateObj);

        return dataArray;
    }

    @Override
    public MoneyAreaDTO getAllData() {
        return chartMapper.getAllData();
    }

    @Override
    public ArrayList<MoneyAreaDTO> getRadar() {
        return chartMapper.getRadar();
    }
}
