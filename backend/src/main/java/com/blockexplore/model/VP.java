package com.blockexplore.model;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class VP{
    // holder对自己VP的签名
    private String proofType;
    private String created;
    private String proofPurpose;
    private String verificationMethod;
    private String jws;

    public JSONObject toJSON(){
        JSONObject proof = new JSONObject();
        proof.put("type", proofType);
        proof.put("created", created);
        proof.put("proofPurpose", proofPurpose);
        proof.put("verificationMethod", verificationMethod);
        proof.put("jws", jws);
        return proof;
    }
}
