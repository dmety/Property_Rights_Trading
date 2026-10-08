package cn.edu.cjxy.iotlink.util;

public class HexUtils {
    /**
     * 将 HEX 字符串转换为字节数组
     * @param hexString HEX 字符串
     * @return 字节数组
     */
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
    public static String stringToHex(String str) {
        StringBuilder hex = new StringBuilder();
        for (char c : str.toCharArray()) {
            hex.append(Integer.toHexString((int) c));
        }
        return hex.toString();
    }
    public static String hexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            sb.append((char) Integer.parseInt(hex.substring(i, i + 2), 16));
        }
        return sb.toString();
    }

    /**
     * 提取指定范围的字节
     * @param hexString HEX 字符串
     * @param startByte 起始字节（索引从 0 开始）
     * @param endByte   结束字节（索引从 0 开始）
     * @return 提取的字节数组
     */
    public static byte[] extractBytesInRange(String hexString, int startByte, int endByte) {
        byte[] byteArray = hexStringToByteArray(hexString);
        if (startByte < 0 || endByte >= byteArray.length || startByte > endByte) {
            throw new IllegalArgumentException("Invalid byte range");
        }
        byte[] result = new byte[endByte - startByte + 1];
        System.arraycopy(byteArray, startByte, result, 0, result.length);
        return result;
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

    /**
     * 将字节数组转换为 HEX 字符串
     * @param bytes 字节数组
     * @return 对应的 HEX 字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    /**
     * 将 HEX 字符串中的指定范围字节转换为十进制（支持负数）
     * @param hexString HEX 字符串
     * @param startByte 起始字节（索引从 0 开始）
     * @param endByte   结束字节（索引从 0 开始）
     * @return 转换后的十进制数值
     */
    public static long hexRangeToDecimal(String hexString, int startByte, int endByte) {
        byte[] extractedBytes = extractBytesInRange(hexString, startByte, endByte);
        return bytesToDecimal(extractedBytes);
    }

    public static void main(String[] args) {
        // 示例 HEX 字符串
        String hexString = "0316";
        // 将字节的 HEX 转换为十进制
        long decimalValue = hexRangeToDecimal(hexString, 0, 1);
        // 输出结果
        System.out.println("Decimal value of extracted HEX range: " + decimalValue);  // 输出 -1
    }
}


