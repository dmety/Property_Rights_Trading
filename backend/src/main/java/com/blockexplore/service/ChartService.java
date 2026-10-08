package com.blockexplore.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.blockexplore.model.MoneyAreaDTO;
import com.blockexplore.utils.Result;
import org.fisco.bcos.sdk.abi.ABICodecException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public interface ChartService {
    Result<Object> getPieData() throws ABICodecException;

    List<Object> getTradeAmount();

    List<List<Integer>> getAllMoneyArea();

    JSONArray getDashboard();

    MoneyAreaDTO getAllData();

    ArrayList<MoneyAreaDTO> getRadar();
}
