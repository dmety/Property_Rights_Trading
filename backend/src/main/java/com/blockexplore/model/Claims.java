package com.blockexplore.model;

import cn.hutool.json.JSONObject;
import com.blockexplore.utils.HttpUtils;
import lombok.Data;

@Data
public class Claims {
    private String userAddr;
    private String userType;
    private String userName;
    private String userOrgan;
    private String tradeType;
    private String claimTime;
    private String userId;
    private String orgId;
    private String publicKey;

    public JSONObject toJSON()
    {
        return new JSONObject(this);
    }
}
