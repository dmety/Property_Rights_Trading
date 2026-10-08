package com.blockexplore.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.mapper.UserDao;
import com.blockexplore.model.User;
import com.blockexplore.request.QRCodeLoginRequest;
import com.blockexplore.service.UserService;
import com.blockexplore.utils.DIDUtil;
import com.blockexplore.utils.KeyUtils;
import com.blockexplore.utils.QRCodeGenerator;
import com.blockexplore.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/*
* 二维码处理,登录二维码
* */
@RestController
@CrossOrigin
@RequestMapping("/qrcode")
public class QRCodeController {
    @Autowired
    UserDao userDao;

    // 状态不共享，每次生成新的二维码时清除之前的状态
    private String qrCodeStatus = "未扫描";  // 用于记录二维码的状态
    private JSONObject vc;  // 用于记录当前的VC

    @Autowired
    UserService userService;

    // 生成二维码
    @GetMapping("/gen")
    public Result<String> genQRCode(@RequestParam String qrCodeId) throws Exception {
        // 每次生成新二维码，重置状态
        qrCodeStatus = "未扫描";
        vc = null;  // 重置vc状态

        // 生成二维码内容
        String qrCodeContent = EnvConfig.BASE_QRCODE_URL + qrCodeId;
        String qrCodePath = "qrcode/" + qrCodeId + ".png";
        QRCodeGenerator.generateQRCode(qrCodeContent, qrCodePath, 300, 300);

        return Result.success(qrCodeId);  // 返回二维码ID
    }

    // 获取二维码图片的Base64编码或查询状态（POST请求）
    @PostMapping("/{code}")
    public Object getQRCode(
            @PathVariable("code") String code,
            @RequestBody QRCodeLoginRequest request) throws Exception {
        System.out.println("QRCode ===>" + request);

        // 每次请求创建一个新的 JSONObject 实例
        JSONObject result = new JSONObject();

        // 查询当前二维码的状态
        if (Boolean.TRUE.equals(Boolean.valueOf(request.getQuery()))) {
            result.set("status", qrCodeStatus);  // 当前二维码状态
            result.set("vc", vc != null ? vc : new JSONObject());  // 当前的VC信息
            return result;
        }

        // 如果没有传递状态参数，返回二维码图片的Base64编码
        if (request.getStatus() == null) {
            String qrCodePath = "QRCode/" + code + ".png";
            File qrCodeFile = new File(qrCodePath);
            if (!qrCodeFile.exists()) {
                return "二维码不存在";
            }
            try (FileInputStream fis = new FileInputStream(qrCodeFile)) {
                byte[] imageBytes = new byte[(int) qrCodeFile.length()];
                fis.read(imageBytes);
                return "data:image/png;base64," + Base64Utils.encodeToString(imageBytes);
            } catch (IOException e) {
                return "读取二维码文件失败: " + e.getMessage();
            }
        }

        // 更新状态并处理vc
        if ("0".equals(request.getStatus())) {
            qrCodeStatus = "0";  // 更新为已扫描
            return "二维码已被扫描";
        } else // 更新状态并处理vc
            if ("1".equals(request.getStatus())) {
                // 解密VC
                String decrypt = KeyUtils.decrypt(EnvConfig.Admin_Private_Key, request.getVc());

                // 验证VC
                if (DIDUtil.verifyVC(decrypt)) {
                    // 解析VC
                    vc = JSONUtil.parseObj(decrypt);

                    // 提取 userId
                    String userName = JSONUtil.parseObj(vc.get("credentialSubject")).getStr("userName");

                    // 通过 userId 查找用户
                    User user = userDao.getUserByName(userName);

                    // 更新二维码状态为 "1" （验证成功）
                    qrCodeStatus = "1";
                    vc = JSONUtil.parseObj(user);

                    // 返回状态和用户信息
                    result.set("status", qrCodeStatus);  // 更新的二维码状态
                    result.set("user", user);  // 返回用户信息
                }

                System.out.println("传递的 VC: " + vc);
                return result;  // 返回状态和用户信息
            } else {
                return "无效的状态值";
            }
    }
}


//    @PostMapping("/{code}")
//    public Object getQRCode(
//            @PathVariable("code") String code,
//            @RequestBody QRCodeLoginRequest request) throws Exception {
//        System.out.println("QRCode ===>"+ request);
//
//        // 每次请求创建一个新的 JSONObject 实例
//        JSONObject result = new JSONObject();
//
//        // 查询当前二维码的状态
//        if (Boolean.TRUE.equals(Boolean.valueOf(request.getQuery()))) {
//            result.set("status", qrCodeStatus);
//            result.set("vc", vc);
//            return result;
//        }
//
//        // 如果没有传递状态参数，返回二维码图片的Base64编码
//        if (request.getStatus() == null) {
//            String qrCodePath = "QRCode/" + code + ".png";
//            File qrCodeFile = new File(qrCodePath);
//            if (!qrCodeFile.exists()) {
//                return "二维码不存在";
//            }
//            try (FileInputStream fis = new FileInputStream(qrCodeFile)) {
//                byte[] imageBytes = new byte[(int) qrCodeFile.length()];
//                fis.read(imageBytes);
//                return "data:image/png;base64," + Base64Utils.encodeToString(imageBytes);
//            } catch (IOException e) {
//                return "读取二维码文件失败: " + e.getMessage();
//            }
//        }
//
//        // 更新状态并处理vc
//        if ("0".equals(request.getStatus())) {
//            qrCodeStatus = "0";  // 更新为已扫描
//            return "二维码已被扫描";
//        } else if ("1".equals(request.getStatus())) {
////            vc = request.getVc();  // 记录传递的vc
//            vc = JSONUtil.parseObj("{\"test\":\"1\"}");
//            // 添加日志记录VC
//            System.out.println("传递的 VC: " + vc);
//
//            // 模拟处理传递的vc
//            JSONObject callBack;
//            if (vc != null && !vc.isEmpty()) {
//                callBack = JSONUtil.parseObj("{\n" +
//                        "  \"id\": 1,\n" +
//                        "  \"loginName\": \"admin\",\n" +
//                        "  \"loginPass\": \"123456\",\n" +
//                        "  \"role\": \"管理员\",\n" +
//                        "  \"organId\": 0,\n" +
//                        "  \"address\": \"0x9aa368fade42d047f1dcb3b04723c447eb188124\",\n" +
//                        "  \"createTime\": \"2024-10-17 15:35:55\",\n" +
//                        "  \"editTime\": \"2024-10-17 15:35:55\",\n" +
//                        "  \"idCard\": 111111111111111111\"\"\n" +
//                        "}");
//            } else {
//                callBack = null;
//            }
//
//            if (callBack != null) {
//                vc = callBack;
//                qrCodeStatus = "1";  // 更新状态为验证成功
//                return "二维码验证成功";
//            } else {
//                qrCodeStatus = "2";  // 更新状态为私钥错误
//                return "私钥错误";
//            }
//        } else {
//            return "无效的状态值";
//        }
//    }



//package com.blockexplore.controller;
//
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import com.blockexplore.callback.QRCodeLoginCallBack;
//import com.blockexplore.config.EnvConfig;
//import com.blockexplore.model.User;
//import com.blockexplore.request.LoginWithOutPassRequest;
//import com.blockexplore.request.QRCodeLoginRequest;
//import com.blockexplore.service.UserService;
//import com.blockexplore.utils.QRCodeGenerator;
//import com.blockexplore.utils.Result;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.Base64Utils;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@RestController
//@CrossOrigin
//@RequestMapping("/qrcode")
//public class QRCodeController {
//    // 由于Spring的特性，这个状态每个请求都会刷新的
//    private String qrCodeStatus = "未扫描";
//    private JSONObject result = new JSONObject();
//
//    @Autowired
//    UserService userService;
//
//    // 生成二维码
//    @GetMapping("/gen") // 生成二维码的路径
//    public Result<String> genQRCode(@RequestParam String qrCodeId) throws Exception {
//        // 生成二维码内容（URL）
//        String qrCodeContent = EnvConfig.BASE_QRCODE_URL + qrCodeId;
//        // 设置初始状态为“未扫描”
//        qrCodeStatus = "未扫描";
//        // 生成二维码并保存
//        String qrCodePath = "qrcode/" + qrCodeId + ".png";
//        QRCodeGenerator.generateQRCode(qrCodeContent, qrCodePath, 300, 300);
//        return Result.success(qrCodeId); // 返回二维码标识符
//    }
//
//    // 获取二维码图片的Base64编码或查询状态（POST请求）
//    @PostMapping("/{code}") // 改为POST接口
//    public Object getQRCode(
//            @PathVariable("code") String code,
//            @RequestBody QRCodeLoginRequest request) throws Exception {
//
//        // 查询当前二维码的状态
//        if (Boolean.TRUE.equals(Boolean.valueOf(request.getQuery()))) {
//            result.set("status",qrCodeStatus);
//            return result; // 返回当前的二维码状态
//        }
//
//        // 如果没有传递状态参数，返回二维码图片的Base64编码
//        if (request.getStatus() == null) {
//            String qrCodePath = "QRCode/" + code + ".png";
//            File qrCodeFile = new File(qrCodePath);
//            if (!qrCodeFile.exists()) {
//                return "二维码不存在";
//            }
//            try (FileInputStream fis = new FileInputStream(qrCodeFile)) {
//                byte[] imageBytes = new byte[(int) qrCodeFile.length()];
//                fis.read(imageBytes);
//                return "data:image/png;base64," + Base64Utils.encodeToString(imageBytes);
//            } catch (IOException e) {
//                return "读取二维码文件失败: " + e.getMessage();
//            }
//        }
//
//        // 根据状态更新状态变量
//        if ("0".equals(request.getStatus())) {
//            qrCodeStatus = "0"; // 更新状态为已扫描
//            return "二维码已被扫描";
//        } else if ("1".equals(request.getStatus())) {
//            JSONObject callBack = JSONUtil.parseObj("{\n" +
//                    "  \"id\": 4,\n" +
//                    "  \"loginName\": \"abc\",\n" +
//                    "  \"loginPass\": \"123\",\n" +
//                    "  \"role\": \"交易受理人员\",\n" +
//                    "  \"organId\": 2,\n" +
//                    "  \"address\": \"0x5119e30c42b7f22900bdeb58194a35607a8edddd\",\n" +
//                    "  \"createTime\": \"2024-10-16 09:25:07\",\n" +
//                    "  \"editTime\": \"2024-10-16 09:25:07\",\n" +
//                    "  \"idCard\": \"\"\n" +
//                    "}\n");
////            QRCodeLoginCallBack callBack = userService.qrCodeLogin(request.getVc());
////            if (result != null) {
////                // 传递VC内用户数据
////                result.set("userInfo",callBack);
////                qrCodeStatus = "1"; // 更新状态为验证成功
////                return "二维码验证成功";
////            } else {
////                qrCodeStatus = "2"; // 更新状态为私钥错误
////                return "私钥错误";
////            }
//            if (callBack!=null){
//                result.set("vc",callBack);
//                qrCodeStatus = "1";
//                return "二维码验证成功";
//            }else {
//                qrCodeStatus = "2";
//                return "私钥错误";
//            }
//        } else {
//            return "无效的状态值";
//        }
//    }
//}
