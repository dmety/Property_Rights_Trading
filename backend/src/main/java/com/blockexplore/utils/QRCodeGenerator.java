package com.blockexplore.utils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import java.nio.file.FileSystems;
import java.nio.file.Path;

public class QRCodeGenerator {

    /**
     * @param data
     * @param path
     * @param width
     * @param height
     * @throws Exception
     */
    public static void generateQRCode(String data, String path, int width, int height) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, width, height);
        Path filePath = FileSystems.getDefault().getPath(path);
        MatrixToImageWriter.writeToPath(matrix, "PNG", filePath);
    }
    //使用例子

//    public static void main(String[] args) throws Exception {
//        //获取到文件当前路径
////        String path = QRCodeGenerator.class.getResource("").getPath();
////        System.out.println(path);
//        String data = "Hello, QR Code!";
//        String path = "qrcode.png";
//        generateQRCode(data, path, 300, 300);
//        System.out.println("QR Code generated: " + path);
//    }
}
