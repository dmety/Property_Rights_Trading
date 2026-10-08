package com.blockexplore;

import com.blockexplore.config.EnvConfig;
import org.fisco.bcos.sdk.crypto.CryptoSuite;
import org.fisco.bcos.sdk.crypto.keypair.CryptoKeyPair;
import org.fisco.bcos.sdk.crypto.signature.SignatureResult;

public class DIDTest {
    private static CryptoSuite cryptoSuite = new CryptoSuite(1);
    private static CryptoKeyPair admin;

    static {
        admin = cryptoSuite.createKeyPair(EnvConfig.Admin_Private_Key);
    }
    public static String sign(String data) {
        String hashData = cryptoSuite.hash(data.trim());  // 确保哈希前后数据一致
        SignatureResult result = cryptoSuite.sign(hashData, admin);
        return result.convertToString();  // 将签名结果转换为字符串
    }

    // 验证签名方法
    public static Boolean verify(String data, String signData) {
        String hashData = cryptoSuite.hash(data.trim());  // 确保哈希一致
        try {
            // 尝试使用公钥验证签名，确保签名格式正确
            return cryptoSuite.verify(admin.getHexPublicKey(), hashData, signData);
        } catch (Exception e) {
            // 捕获异常并输出详细信息，帮助调试
            System.out.println("Verify with SM2 failed: " + e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        String data = "你好";
        String datab = "你好";
        String sign1 = sign(data);
        System.out.println(sign(datab));
        System.out.println(sign1);
        System.out.println(verify(data, sign1));
    }
}
