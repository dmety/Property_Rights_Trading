package com.blockexplore.controller;

import cn.hutool.json.JSONObject;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.request.ReViewRequest;
import com.blockexplore.service.UserService;
import com.blockexplore.utils.DIDUtil;
import com.blockexplore.utils.QRCodeGenerator;
import com.blockexplore.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
/*
* 审核流程中的二维码生成与 VP验证
* */
@RestController
@CrossOrigin
@RequestMapping("/review")
public class ReviewController {

    private String qrCodeStatus = "未扫描";  // 用于记录二维码的状态

    @Autowired
    UserService userService;

    // 生成二维码
    @GetMapping("/gen")
    public Result<String> genQRCode(@RequestParam String qrCodeId) throws Exception {
        // 每次生成新二维码，重置状态
        qrCodeStatus = "未扫描";

        // 生成二维码内容
        String qrCodeContent = EnvConfig.REVIEW_QRCODE_URL + qrCodeId;
        String qrCodePath = "qrcode/" + qrCodeId + ".png";
        QRCodeGenerator.generateQRCode(qrCodeContent, qrCodePath, 300, 300);

        return Result.success(qrCodeId);  // 返回二维码ID
    }

    // 获取二维码图片的Base64编码或查询状态
    @PostMapping("/{code}")
    public Result<Object> getQRCode(
            @PathVariable("code") String code, @RequestBody ReViewRequest request) throws Exception {

        // 每次请求创建一个新的 JSONObject 实例
        JSONObject result = new JSONObject();

        // 查询当前二维码的状态（只有当传递了 query 参数时）
        if ("true".equals(request.getQuery())) {
            result.set("status", qrCodeStatus);  // 返回当前二维码状态
            return Result.success(result);
        }

        // 如果没有传递 status 参数，返回二维码图片的Base64编码
        if (request.getStatus() == null) {
            String qrCodePath = "qrcode/" + code + ".png";
            File qrCodeFile = new File(qrCodePath);
            if (!qrCodeFile.exists()) {
                return Result.failure("二维码不存在");
            }
            try (FileInputStream fis = new FileInputStream(qrCodeFile)) {
                byte[] imageBytes = new byte[(int) qrCodeFile.length()];
                fis.read(imageBytes);
                String base64QRCode = "data:image/png;base64," + Base64Utils.encodeToString(imageBytes);
                return Result.success(base64QRCode);
            } catch (IOException e) {
                return Result.failure("读取二维码文件失败: " + e.getMessage());
            }
        }

        // 更新状态
        if ("0".equals(request.getStatus())) {
            qrCodeStatus = "0";  // 更新为已扫描
            return Result.success("二维码已被扫描");
        } else if ("1".equals(request.getStatus())) {
            // 判断验证签名是否成功
            if (DIDUtil.verifyVP(request.getVp())){
                qrCodeStatus = "1";  // 更新状态为验证成功
                result.set("status", qrCodeStatus);  // 返回更新后的状态
                return Result.success(result);
            }else {
                return Result.failure("无效的VP");
            }
        } else {
            return Result.failure("无效的状态值");
        }
    }
}
