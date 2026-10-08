package com.blockexplore.utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Base64 图像转换工具类 (JDK 1.8 兼容版)
 * 提供图片与 Base64 编码相互转换的功能
 */
public class ImageBase64Utils {

    /**
     * 将图片文件转换为 Base64 编码
     *
     * @param filePath 图片文件路径
     * @return Base64 编码字符串
     * @throws IOException 文件读取异常
     */
    public static String imageToBase64(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将图片文件转换为 Base64 编码
     *
     * @param file 图片文件对象
     * @return Base64 编码字符串
     * @throws IOException 文件读取异常
     */
    public static String imageToBase64(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将 MultipartFile 转换为 Base64 编码
     *
     * @param file Spring 的 MultipartFile 对象
     * @return Base64 编码字符串
     * @throws IOException 文件读取异常
     */
    public static String imageToBase64(MultipartFile file) throws IOException {
        return Base64.getEncoder().encodeToString(file.getBytes());
    }

    /**
     * 将字节数组转换为 Base64 编码
     *
     * @param bytes 图片字节数组
     * @return Base64 编码字符串
     */
    public static String bytesToBase64(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * 将 Base64 编码转换为图片文件
     *
     * @param base64     Base64 编码字符串
     * @param outputPath 输出文件路径
     * @throws IOException 文件写入异常
     */
    public static void base64ToImage(String base64, String outputPath) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
            fos.write(decodedBytes);
        }
    }

    /**
     * 将 Base64 编码转换为图片文件
     *
     * @param base64 Base64 编码字符串
     * @param output 输出文件对象
     * @throws IOException 文件写入异常
     */
    public static void base64ToImage(String base64, File output) throws IOException {
        byte[] decodedBytes = Base64.getDecoder().decode(base64);
        try (FileOutputStream fos = new FileOutputStream(output)) {
            fos.write(decodedBytes);
        }
    }

    /**
     * 将 Base64 编码转换为字节数组
     *
     * @param base64 Base64 编码字符串
     * @return 解码后的字节数组
     */
    public static byte[] base64ToBytes(String base64) {
        return Base64.getDecoder().decode(base64);
    }

    /**
     * 将 Base64 编码转换为可直接显示的 Data URL
     *
     * @param base64    Base64 编码字符串
     * @param mimeType 文件类型（如 "image/jpeg"）
     * @return Data URL 字符串
     */
    public static String toDataUrl(String base64, String mimeType) {
        return "data:" + mimeType + ";base64," + base64;
    }

    /**
     * 清除 Base64 字符串中的 Data URL 前缀
     *
     * @param dataUrl 带有 Data URL 前缀的字符串
     * @return 纯净的 Base64 编码
     */
    public static String cleanDataUrlPrefix(String dataUrl) {
        if (dataUrl == null) return null;
        int commaIndex = dataUrl.indexOf(',');
        return commaIndex != -1 ? dataUrl.substring(commaIndex + 1) : dataUrl;
    }

    /**
     * 安全转换 MultipartFile 到 Base64（带异常处理）
     *
     * @param file MultipartFile 对象
     * @return Base64 编码或 null（出错时）
     */
    public static String safeImageToBase64(MultipartFile file) {
        try {
            return imageToBase64(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 安全保存 Base64 到图片文件（带异常处理）
     *
     * @param base64     Base64 编码
     * @param outputPath 输出路径
     * @return 是否成功
     */
    public static boolean safeBase64ToImage(String base64, String outputPath) {
        try {
            base64ToImage(base64, outputPath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

