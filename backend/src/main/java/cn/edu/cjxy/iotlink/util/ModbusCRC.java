package cn.edu.cjxy.iotlink.util;

public class ModbusCRC {
    //ModbusCRC-16计算（低字节在前，高字节在后）
    public static String calculateCRC(byte[] data) {
        int crc = 0xFFFF; //初始化CRC为0xFFFF
        //遍历数据字节
        for (int i = 0; i < data.length; i++) {
            crc ^= (data[i] & 0xFF); //将数据字节与当前CRC值进行异或操作
            //计算CRC
            for (int j = 8; j > 0; j--) {
                if ((crc & 0x0001) != 0) {
                    crc >>= 1; //右移1位
                    crc ^= 0xA001; //多项式0xA001（x^16 + x^15 + x^2 + 1）
                }
                else {
                    crc >>= 1; //右移1位
                }
            }
        }
        //CRC值转换为低字节在前，高字节在后的顺序
        int lowByte = crc & 0xFF; //取低字节
        int highByte = (crc >> 8) & 0xFF; //取高字节
        //返回拼接后的CRC字符串：低字节在前
        return String.format("%02X%02X", lowByte, highByte);
    }

    //拼接多个字节数组
    public static byte[] concatenate(byte[]... arrays) {
        int totalLength = 0;
        for (byte[] array : arrays) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int currentPos = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, currentPos, array.length);
            currentPos += array.length;
        }
        return result;
    }

    public static void main(String[] args) {
        //定义数据各个部分
        byte[] addressCode = {0x01}; //地址码
        byte[] functionCode = {0x03}; //功能码
        byte[] startAddress = {0x00, 0x1E}; //起始地址 (0x00 0x20)
        byte[] dataLength = {0x00, 0x03}; //数据长度 (0x00 0x01)
        //拼接所有部分
        byte[] dataToChecksum = concatenate(addressCode, functionCode, startAddress, dataLength);
        dataToChecksum = HexUtils.hexStringToByteArray("010300000003");
        //计算ModbusCRC校验码（低字节在前）
        String crc = calculateCRC(dataToChecksum);
        //输出CRC校验码
        System.out.println("Modbus CRC 校验码: " + crc);
    }
}

