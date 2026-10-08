package com.blockexplore.model;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class VC {
    // Credential Metadata
    private List<String> context = Arrays.asList(
            "https://www.w3.org/2018/credentials/v1",
            "https://www.w3.org/2018/credentials/examples/v1"
    );
    private String id;
    private String[] type;
    private String issuer;
    private String issuanceDate;

    // Claims
    private String userAddr;
    private String userType;
    private String userName;
    private String userOrgan;
    private String tradeType;
    private String claimTime;
    private String userId;
    private String orgId;

    // Proof
    private String proofType;
    private String created;
    private String proofPurpose;
    private String verificationMethod;
    private String jws;

    // 将 VC 对象转换为 JSON 字符串
    public String toJSON() {
        JSONObject vcJson = new JSONObject();

        // Credential Metadata
        vcJson.put("@context", new JSONArray(context));
        vcJson.put("id", id);
        vcJson.put("type", new JSONArray(Arrays.asList(type)));
        vcJson.put("issuer", issuer);
        vcJson.put("issuanceDate", issuanceDate);

        // Claims
        JSONObject credentialSubject = new JSONObject();
        credentialSubject.put("userAddr", userAddr);
        credentialSubject.put("userType", userType);
        credentialSubject.put("userName", userName);
        credentialSubject.put("userOrgan", userOrgan);
        credentialSubject.put("tradeType", tradeType);
        credentialSubject.put("claimTime", claimTime);
        credentialSubject.put("userId", userId);
        credentialSubject.put("orgId", orgId);
        vcJson.put("credentialSubject", credentialSubject);

        // Proof
        JSONObject proof = new JSONObject();
        proof.put("type", proofType);
        proof.put("created", created);
        proof.put("proofPurpose", proofPurpose);
        proof.put("verificationMethod", verificationMethod);
        proof.put("jws", jws);
        vcJson.put("proof", proof);

        return vcJson.toString();
    }
}
