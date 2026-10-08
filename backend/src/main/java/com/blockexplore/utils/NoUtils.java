package com.blockexplore.utils;

import com.blockexplore.mapper.ProjectDao;
import com.blockexplore.mapper.RightDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component  // 让 Spring 管理这个类
public class NoUtils {

    private final RightDao rightDao;
    private final ProjectDao projectDao;

    // 构造函数注入 RightDao 和 IotProjectDao
    @Autowired
    public NoUtils(RightDao rightDao, ProjectDao projectDao) {
        this.rightDao = rightDao;
        this.projectDao = projectDao;
    }

    // 生成编号的方法
    public String generateNo(String front, String tradeType) {
        // 获取数据库中的最大编号
        String maxNo = rightDao.getMaxRightNo();

        // 如果 maxNo 为 null，使用默认编号
        if (maxNo == null || maxNo.length() < 5) {
            return front + TimeUtils.getCurrentYear() + tradeType + "00001"; // 默认编号
        }

        // 提取数字部分，例如 CQ2024A00032 -> 00032
        String numberPart = maxNo.substring(maxNo.length() - 5);

        // 将数字部分转换为整数，并递增1
        int newNumber;
        try {
            newNumber = Integer.parseInt(numberPart) + 1;
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法解析编号: " + maxNo, e);
        }

        // 格式化数字部分，确保是5位数字
        String newNumberPart = String.format("%05d", newNumber);

        // 生成新的编号
        String newNo = front + TimeUtils.getCurrentYear() + tradeType + newNumberPart;

        return newNo;
    }


    public String generateProjectNo(String front, String tradeType) {
        // 获取数据库中的最大编号
        String maxNo = projectDao.getMaxRightNo();

        // 如果 maxNo 为 null，使用默认编号
        if (maxNo == null || maxNo.length() < 5) {
            return front + TimeUtils.getCurrentYear() + tradeType + "00001"; // 默认编号
        }

        // 提取数字部分，例如 CQ2024A00032 -> 00032
        String numberPart = maxNo.substring(maxNo.length() - 5);

        // 将数字部分转换为整数，并递增1
        int newNumber;
        try {
            newNumber = Integer.parseInt(numberPart) + 1;
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法解析编号: " + maxNo, e);
        }

        // 格式化数字部分，确保是5位数字
        String newNumberPart = String.format("%05d", newNumber);

        // 生成新的编号
        String newNo = front + TimeUtils.getCurrentYear() + tradeType + newNumberPart;

        return newNo;
    }
    public String generateContractNo(String front, String tradeType) {
        // 获取数据库中的最大编号
        String maxNo = projectDao.getMaxContractCode();

        // 如果 maxNo 为 null，使用默认编号
        if (maxNo == null || maxNo.length() < 5) {
            return front + TimeUtils.getCurrentYear() + tradeType + "00001"; // 默认编号
        }

        // 提取数字部分，例如 CQ2024A00032 -> 00032
        String numberPart = maxNo.substring(maxNo.length() - 5);

        // 将数字部分转换为整数，并递增1
        int newNumber;
        try {
            newNumber = Integer.parseInt(numberPart) + 1;
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法解析编号: " + maxNo, e);
        }

        // 格式化数字部分，确保是5位数字
        String newNumberPart = String.format("%05d", newNumber);

        // 生成新的编号
        String newNo = front + TimeUtils.getCurrentYear() + tradeType + newNumberPart;

        return newNo;
    }
    public String generateAppraisalCertNo(String front, String tradeType) {
        // 获取数据库中的最大编号
        String maxNo = projectDao.getMaxAppraisaCertNo();

        // 如果 maxNo 为 null，使用默认编号
        if (maxNo == null || maxNo.length() < 5) {
            return front + TimeUtils.getCurrentYear() + tradeType + "00001"; // 默认编号
        }

        // 提取数字部分，例如 CQ2024A00032 -> 00032
        String numberPart = maxNo.substring(maxNo.length() - 5);

        // 将数字部分转换为整数，并递增1
        int newNumber;
        try {
            newNumber = Integer.parseInt(numberPart) + 1;
        } catch (NumberFormatException e) {
            throw new RuntimeException("无法解析编号: " + maxNo, e);
        }

        // 格式化数字部分，确保是5位数字
        String newNumberPart = String.format("%05d", newNumber);

        // 生成新的编号
        String newNo = front + TimeUtils.getCurrentYear() + tradeType + newNumberPart;

        return newNo;
    }
}
