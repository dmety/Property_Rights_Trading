package com.example.app.Utils;

import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class EncryptionUtil {
    public static String hashData(String data) {
        try {
            // 创建 SHA-256 消息摘要对象
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // 计算哈希值
            byte[] hashBytes = digest.digest(data.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0'); // 确保每个字节都用两个字符表示
                hexString.append(hex);
            }

            return hexString.toString(); // 返回哈希后的字符串
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e("Error", "哈希算法不存在: " + e.getMessage());
            return null;
        }
    }
}
