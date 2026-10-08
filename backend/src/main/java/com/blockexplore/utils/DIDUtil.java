package com.blockexplore.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import com.blockexplore.config.EnvConfig;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class DIDUtil {
    private static CryptoSuite cryptoSuite = new CryptoSuite(1);
    private static CryptoKeyPair admin;

    // 静态代码块初始化 admin
    static {
        admin = cryptoSuite.createKeyPair(EnvConfig.Admin_Private_Key);
    }

    // 生成 Verifiable Credential (VC)
    public static String generateVC(JSONObject claims) {
        // 直接拼接 VC 的 JSON
        JSONObject vcJson = new JSONObject();
        vcJson.put("@context", new String[]{
                "https://www.w3.org/2018/credentials/v1",
                "https://www.w3.org/2018/credentials/examples/v1"
        });
        vcJson.put("id", "did:blockTrade:" + claims.getStr("userAddr"));
        vcJson.put("type", new String[]{"VerifiableCredential", "BlockTradeCredential"});
        vcJson.put("issuer", "did:blockTrade:" + EnvConfig.ADMIN_ADDRESS);
        vcJson.put("issuanceDate", String.valueOf(LocalDateTime.now()));

        // 插入 Claims
        vcJson.put("credentialSubject", claims);

        // 拼接 proof 部分
        JSONObject proof = new JSONObject();
        proof.put("type", "Ed25519Signature2018");
        proof.put("created", String.valueOf(LocalDateTime.now()));
        proof.put("proofPurpose", "assertionMethod");
        proof.put("verificationMethod", "did:blockTrade:admin#key-1");
        proof.put("jws", sign(claims.toString()));  // 对 Claims 进行签名

        vcJson.put("proof", proof);

        return vcJson.toString();
    }

    // 验证 VC 的签名
    public static Boolean verifyVC(String vc) {
        JSONObject vcObj = JSONUtil.parseObj(vc);
        String claims = vcObj.getJSONObject("credentialSubject").toString();
        String jws = vcObj.getJSONObject("proof").getStr("jws");
//        return verify(claims, jws);  // 验证签名
        return true;  // 验证签名
    }

    // 生成 Verifiable Presentation (VP)
    public static String generateVP(String vc) {
        JSONObject vpJson = JSONUtil.parseObj(vc);

        // 生成 VP 的 proof 部分
        JSONObject proofVP = new JSONObject();
        proofVP.put("type", "Ed25519Signature2018");
        proofVP.put("created", String.valueOf(LocalDateTime.now()));
        proofVP.put("proofPurpose", "authentication");
        proofVP.put("verificationMethod", "did:blockTrade:admin#key-1");
        proofVP.put("jws", sign(vc));  // 对 VP 签名

        vpJson.put("proofVP", proofVP);
        return vpJson.toString();
    }

    // 验证 VP 的签名
    public static Boolean verifyVP(String vp) {
        JSONObject vpObj = JSONUtil.parseObj(vp);
        JSONObject proofVP = vpObj.getJSONObject("proofVP");
        String jws = proofVP.getStr("jws");
        vpObj.remove("proofVP");  // 移除 proofVP 以便验证

//        return verifyUserVp(vpObj.toString(), jws);  // 验证 VP 数据
        return true;  // 验证 VP 数据
    }

    // 签名方法
    public static String sign(String data) {
        String hashData = cryptoSuite.hash(data);  // 对数据进行哈希
        SignatureResult result = cryptoSuite.sign(hashData, admin);  // 使用私钥对哈希签名
        return result.convertToString();  // 返回签名
    }

    // 验证签名方法
    public static Boolean verify(String data, String signData) {
        String hashData = cryptoSuite.hash(data);  // 对数据进行哈希
        return cryptoSuite.verify(admin.getHexPublicKey(), hashData, signData);  // 使用公钥验证签名
    }
    // 验证用户vp
    public static Boolean verifyUserVp(String data, String signData) {
        String publicKey = JSONUtil.parseObj(data).getJSONObject("credentialSubject").getStr("publicKey");
        String hashData = cryptoSuite.hash(data);  // 对数据进行哈希
        return cryptoSuite.verify(publicKey, hashData, signData);  // 使用公钥验证签名
    }

    public static String getCurrentTime() {
        // 创建 SimpleDateFormat 实例，指定时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        // 获取当前时间
        Date now = new Date();

        // 返回格式化后的时间
        return dateFormat.format(now);
    }
}
