package com.blockexplore.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    // 获取当前的完整日期时间（格式：yyyy-MM-dd HH:mm:ss）
    public static String getCurrentDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    // 获取当前的日期（格式：yyyy-MM-dd）
    public static String getCurrentDate() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return currentDateTime.format(formatter);
    }

    // 获取当前的时间（格式：HH:mm:ss）
    public static String getCurrentTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    // 获取当前的年份
    public static int getCurrentYear() {
        return LocalDateTime.now().getYear();
    }

    // 获取当前的月份
    public static int getCurrentMonth() {
        return LocalDateTime.now().getMonthValue();
    }

    // 获取当前的日期（天）
    public static int getCurrentDay() {
        return LocalDateTime.now().getDayOfMonth();
    }

    // 获取当前的小时
    public static int getCurrentHour() {
        return LocalDateTime.now().getHour();
    }

    // 获取当前的分钟
    public static int getCurrentMinute() {
        return LocalDateTime.now().getMinute();
    }

    // 获取当前的秒
    public static int getCurrentSecond() {
        return LocalDateTime.now().getSecond();
    }
}
