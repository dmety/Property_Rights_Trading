package cn.edu.cjxy.iotlink.util;

public class HexUtilTest {
    //请帮我编写十六进制到十进制的转换代码
    public static void main(String[] args) {
        String hex = "0103";
        System.out.println(bytesToDecimal(hexStringToByteArray(hex)));
    }

    //将字节数组转换为 HEX 字符串
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    //将 HEX 字符串转换为字节数组
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 将字节数组转换为十进制数值（支持负数）
     * @param bytes 字节数组
     * @return 对应的十进制数值
     */
    public static long bytesToDecimal(byte[] bytes) {
        long decimalValue = 0;
        for (int i = 0; i < bytes.length; i++) {
            decimalValue = (decimalValue << 8) | (bytes[i] & 0xFF);
        }
        // 如果字节数组表示的数值是负数，转换为补码并处理符号扩展
        if (bytes.length > 0 && (bytes[0] & 0x80) != 0) {
            decimalValue |= (-1L << (bytes.length * 8)); // 扩展符号位
        }
        return decimalValue;
    }

}
