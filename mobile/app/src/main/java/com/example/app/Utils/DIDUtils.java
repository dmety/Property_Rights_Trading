package com.example.app.Utils;

import android.util.Log;

import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

public class DIDUtils {
    private static CryptoSuite cryptoSuite = new CryptoSuite(1);
    private static CryptoKeyPair user;

    public DIDUtils(String privateKey) {
        user = cryptoSuite.getKeyPairFactory().derivePublicKey(privateKey);
    }
    public static String sign(String data) {
        String hashData = cryptoSuite.hash(data);  // 对数据进行哈希
        SignatureResult result = cryptoSuite.sign(hashData, user);  // 使用私钥对哈希签名
        return result.convertToString();  // 返回签名
    }
    public static Boolean verify(String data, String signData) {
        String hashData = cryptoSuite.hash(data);  // 对数据进行哈希
        return cryptoSuite.verify(user.getHexPublicKey(), hashData, signData);  // 使用公钥验证签名
    }
    public static String generateVP(String vc) throws JSONException {
        JSONObject vpJson = new JSONObject(vc);

        // 生成 VP 的 proof 部分
        JSONObject proofVP = new JSONObject();
        proofVP.put("type", "Ed25519Signature2018");
        proofVP.put("created", getCurrentTime());
        proofVP.put("proofPurpose", "authentication");
        proofVP.put("verificationMethod", "did:blockTrade:admin#key-1");
        proofVP.put("jws", sign(vc));  // 对 VP 签名

        vpJson.put("proofVP", proofVP);
        return vpJson.toString();
    }

    // 验证 VP 的签名
    public static Boolean verifyVP(String vp) throws JSONException {
        JSONObject vpObj = new JSONObject(vp);
        JSONObject proofVP = vpObj.getJSONObject("proofVP");
        String jws = proofVP.getString("jws");
        vpObj.remove("proofVP");  // 移除 proofVP 以便验证

        return verify(vpObj.toString(), jws);  // 验证 VP 数据
    }
    public static String getCurrentTime() {
        // 创建 SimpleDateFormat 实例，指定时间格式
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

        // 获取当前时间
        Date now = new Date();

        // 返回格式化后的时间
        return dateFormat.format(now);
    }

    // 静态代码块初始化 admin
//    static {
//    }

    // 新增：根据私钥构造 CryptoKeyPair
//    public static CryptoKeyPair createUserFromPrivateKey() {
//        SignatureResult result = cryptoSuite.sign(cryptoSuite.hash("test"), user);
//        Log.d("SignatureResult ===> " ,result.convertToString());
//        Boolean isVal = cryptoSuite.verify(user.getHexPublicKey(),cryptoSuite.hash("test"),result.convertToString());
//        Log.d("Verify ===> " ,isVal.toString());
//        return user;
//    }

//    public String sign(String content) {
//        String hash_content = cryptoSuite.hash(content);
//        SignatureResult result = cryptoSuite.sign(hash_content, admin);
//        Log.d("TAG", result.convertToString());
//        return null;
//    }
//
//    public static String generateVP(String vc) throws JSONException {
//        JSONObject vpJson = new JSONObject(vc);
//
//        // 生成 VP 的 proof 部分
//        JSONObject proofVP = new JSONObject();
//        proofVP.put("type", "Ed25519Signature2018");
//        proofVP.put("created", String.valueOf(LocalDateTime.now()));
//        proofVP.put("proofPurpose", "authentication");
//        proofVP.put("verificationMethod", "did:blockTrade:admin#key-1");
//        proofVP.put("jws", sign(vc));  // 对 VP 签名
//
//        vpJson.put("proofVP", proofVP);
//        return vpJson.toString();
//    }
}

