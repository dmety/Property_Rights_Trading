package com.blockexplore;

import com.blockexplore.model.Claims;
import com.blockexplore.utils.DIDUtil;
import cn.hutool.json.JSONObject;

public class Main {
    public static void main(String[] args) {
        String sign = DIDUtil.sign("\"你好");
        System.out.println(DIDUtil.verify("\"你好",sign));
    }
}
