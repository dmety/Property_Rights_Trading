package com.blockexplore.mapper;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import com.blockexplore.model.CountMod;
import com.blockexplore.model.MoneyAreaDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface ChartMapper {
    public List<Integer> getPieData();

    List<Integer> getTradeAmount();

//    List<Map<String, Object>> getAllMoneyArea();
    List<MoneyAreaDTO> getAllMoneyArea();

    CountMod getDashboard();

    MoneyAreaDTO getAllData();

    ArrayList<MoneyAreaDTO> getRadar();
}
