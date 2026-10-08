package com.example.app.Utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件处理工具类
 * 功能：处理URI到文件对象的转换，兼容不同Android版本和URI类型
 */
public class FileUtils {
    private static final String TAG = "FileUtils"; // 日志标签

    /**
     * 从URI获取文件对象
     * @param context 上下文对象
     * @param uri 文件URI
     * @return 对应的File对象
     * @throws IOException 文件操作异常
     */
    @SuppressLint("Range")
    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        if (uri == null) return null;

        // 根据Android版本选择不同的处理方式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10及以上使用ContentResolver处理
            return handleUriForAndroid10AndAbove(context, uri);
        } else {
            // Android 10以下使用传统方式处理
            return handleUriBelowAndroid10(context, uri);
        }
    }

    /**
     * 处理Android 10及以上版本的URI转换（Scoped Storage）
     * @param context 上下文对象
     * @param uri 文件URI
     * @return 临时文件对象
     * @throws IOException 文件操作异常
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private static File handleUriForAndroid10AndAbove(Context context, Uri uri) throws IOException {
        // 获取文件名
        String fileName = getFileName(context, uri);
        // 在缓存目录创建临时文件
        File tempFile = new File(context.getCacheDir(), fileName);

        // 使用try-with-resources确保流关闭
        try (InputStream inputStream = context.getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {

            if (inputStream != null) {
                byte[] buffer = new byte[4 * 1024]; // 4KB的缓冲区
                int read;
                // 读取输入流并写入临时文件
                while ((read = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, read);
                }
                // 刷新输出流
                outputStream.flush();
                return tempFile;
            }
        }
        return null;
    }

    /**
     * 处理Android 10以下版本的URI转换
     * @param context 上下文对象
     * @param uri 文件URI
     * @return 对应的File对象
     */
    @SuppressWarnings("deprecation")
    private static File handleUriBelowAndroid10(Context context, Uri uri) throws IOException {
        // 处理content协议的URI
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 查询媒体库获取真实路径
            String[] projection = { MediaStore.Images.Media.DATA };
            try (Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    // 从游标中获取文件路径
                    return new File(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                }
            }
        }
        // 处理file协议的URI
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return new File(uri.getPath());
        }
        return null;
    }

    /**
     * 获取文件名
     * @param context 上下文对象
     * @param uri 文件URI
     * @return 文件名，如果获取失败返回默认文件名
     */
    public static String getFileName(Context context, Uri uri) {
        // 默认文件名
        String fileName = "temp_file_" + System.currentTimeMillis();
        try (Cursor cursor = context.getContentResolver().query(uri,
                new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null)) {
            // 从媒体库查询文件名
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(0);
            }
        } catch (Exception e) {
            Log.e(TAG, "获取文件名失败", e);
        }
        return fileName;
    }
}
