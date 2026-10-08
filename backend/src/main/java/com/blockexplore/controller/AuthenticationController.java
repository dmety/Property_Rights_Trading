package com.blockexplore.controller;

import cn.hutool.json.JSONArray;
import org.springframework.core.io.ClassPathResource;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.blockexplore.config.EnvConfig;
import com.blockexplore.mapper.ProjectDao;
import com.blockexplore.model.AppraisalCertInfo;
import com.blockexplore.utils.*;
import com.blockexplore.utils.seal.Seal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;

/*
* 身份验证
* 处理与鉴证书（认证证书）相关的图像上传、签名、验证和信息存储
* */
@RestController
@RequestMapping("/cert")
@CrossOrigin(origins = "*")
public class AuthenticationController {
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    NoUtils noUtils;

    // 接收上传的图片和数据，并读取元数据，图片上传并添加数字签名与印章
    @PostMapping("/signUpload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") String file,
            @RequestParam("data") String data) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取上传文件的字节数组
            if (file.startsWith("data:image")) {
                file = file.substring(file.indexOf(",") + 1);
            }
            byte[] imageBytes = Base64.getDecoder().decode(file);

            // 将字节数组转换为输入流
            InputStream is = new ByteArrayInputStream(imageBytes);

            // 将输入流转换为BufferedImage
            BufferedImage buffImg = ImageIO.read(is);
            JSONObject dataObj = JSONUtil.parseObj(data);

            String jzsCode = noUtils.generateAppraisalCertNo("JZS","X");
            dataObj.set("bookCode",jzsCode);

            // 画章子，获取印章图片的字节数组
//            byte[] sealBuffer = Seal.getSealImg("农权交易链上通", "农权交易链上通");
//            BufferedImage sealImg = ImageIO.read(new ByteArrayInputStream(sealBuffer));
            // 取章子
            ClassPathResource resource = new ClassPathResource("static/signature.png");
            InputStream inputStream = resource.getInputStream();

            // 将输入流转化为BufferedImage
            BufferedImage Img = ImageIO.read(inputStream);
            BufferedImage sealImg = resizeImage(Img, 130,130);


            // 创建 Graphics2D 对象以修改图片
            Graphics2D g2d = buffImg.createGraphics();
            FontMetrics fontMetrics = g2d.getFontMetrics();
            // 设置印章绘制的位置（右下角），可根据需求调整
//            int sealX = buffImg.getWidth() - sealImg.getWidth() - 10; // 距离右边10px
            int sealX = 160;
            int sealY = buffImg.getHeight() - sealImg.getHeight() - 80; // 距离下边10px

            // 将印章图片绘制到目标图片上
            g2d.drawImage(sealImg, sealX, sealY, null);
            g2d.setColor(Color.BLACK);
            g2d.drawString(jzsCode,145, fontMetrics.getAscent() + 65);
            g2d.dispose(); // 完成绘制，释放资源


            // 可选：处理数据写入（如有需要）
            Map<String, String> dataMap = new HashMap<>();
            dataMap.put("BookInfo",dataObj.toString());
            dataMap.put("BookSign", DIDUtil.sign(dataObj.toString()));
            byte[] updatedImageData = ImageUtil.writeCustomData(buffImg, dataMap); // 使用工具类写入数据
            // 构造鉴证书的信息
            JSONObject info = JSONUtil.parseObj(data);
            AppraisalCertInfo appraisalCertInfo = new AppraisalCertInfo();
            appraisalCertInfo.setAppraisalCertNo(jzsCode);
//            System.out.println("SignImg===>"+"data:image/png;base64," + Base64.getEncoder().encodeToString(updatedImageData)+"<===end");、
            // 地
            appraisalCertInfo.setAppraisalData("data:image/png;base64," + Base64.getEncoder().encodeToString(updatedImageData));
            appraisalCertInfo.setProjectCode(info.getStr("projectCode"));
            appraisalCertInfo.setUserId(Integer.parseInt(info.getStr("userId")));
            // 摘要上链
            addAppraisalCertInfo(appraisalCertInfo);
            // 将更新后的图片返回为Base64
            response.put("status", "success");
            response.put("code", "200");
            response.put("message", "Image uploaded and seal applied successfully");
            response.put("signImg", "data:image/png;base64," + Base64.getEncoder().encodeToString(updatedImageData)); // 返回更新后的图片数据

        } catch (IOException e) {
            response.put("status", "error");
            response.put("message", "Failed to upload image: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok(response);
    }
    // 签名 鉴证书，验证上传的鉴证书图片
    @PostMapping("/verUpload")
    public ResponseEntity<Map<String, Object>> readImageData(@RequestParam("file") MultipartFile file,@RequestParam("projectCode") String projectCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            byte[] imageData = file.getBytes();
            String bookSign = ImageUtil.readCustomData(imageData, "BookSign"); // 读取自定义数据
            String bookInfo = ImageUtil.readCustomData(imageData, "BookInfo");
            byte[] fileBytes = file.getBytes();
            String verData = Base64.getEncoder().encodeToString(fileBytes);
            // 检查是否成功读取到数据
            if (bookSign == null || bookInfo == null) {
                response.put("code", "500");
                response.put("status", "error");
                response.put("message", "Missing required custom data in the image.");
                return ResponseEntity.status(400).body(response); // 返回400状态码
            }
            List<Object> param = new ArrayList<>();
            // 通过projectCode
            param.add(projectCode);
            JSONArray result = JSONUtil.parseArray(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"getAppraisalCertFields",param));
            System.out.println("Authentication === > " + result.getStr(1));
            System.out.println("VerResult ===>" + getStringHash("data:image/png;base64,"+verData));
            // 进行验证
            if (!result.getStr(1).equals(getStringHash("data:image/png;base64,"+verData))){
                response.put("code", "500");
                response.put("status", "error");
                response.put("message", "Verification failed.");
                return ResponseEntity.status(401).body(response);
            }
            Boolean isTrue = DIDUtil.verify(bookInfo, bookSign);
            System.out.println("isTrue ===>" + isTrue);
            // 只有在验证成功时才返回200状态
            if (isTrue) {
                System.out.println("验证成功");
                response.put("status", "success");
                response.put("Sign", true);
                String transfereeCardNo = maskID(JSONUtil.parseObj(bookInfo).getStr("transfereeCardNo"));
                String transferorCardNo = maskID(JSONUtil.parseObj(bookInfo).getStr("transferorCardNo"));
                response.put("BookInfo", JSONUtil.parseObj(bookInfo).set("transfereeCardNo",transfereeCardNo).set("transferorCardNo",transferorCardNo)); // 返回 BookInfo
                return ResponseEntity.ok(response);
            } else {
                response.put("code", "500");
                response.put("status", "error");
                response.put("message", "Signature verification failed.");
                return ResponseEntity.status(401).body(response); // 返回401状态码
            }

        } catch (IOException e) {
            response.put("code", "500");
            response.put("status", "error");
            response.put("message", "Failed to read image data: " + e.getMessage());
            return ResponseEntity.status(500).body(response); // 返回500状态码
        } catch (Exception e) {
            response.put("code", "500");
            response.put("status", "error");
            response.put("message", "An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(response); // 返回500状态码
        }
    }
    // 给项目添加鉴证书，将鉴证书信息上链
    public Object addAppraisalCertInfo(AppraisalCertInfo appraisalCertInfo) {
        List<Object> params = new ArrayList<>();
        params.add(appraisalCertInfo.getProjectCode());
        params.add(appraisalCertInfo.getAppraisalCertNo());
        params.add(getStringHash(appraisalCertInfo.getAppraisalData()));
        params.add(appraisalCertInfo.getUserId());
        projectDao.saveAppraisalCertInfo(appraisalCertInfo.getAppraisalCertNo(),appraisalCertInfo.getProjectCode());
        // 发送 鉴证书
        JSONObject result = JSONUtil.parseObj(HttpUtils.projectRequest2(EnvConfig.ADMIN_ADDRESS,"addAppraisalCertInfo",params));
        if (result.getBool("statusOK")){
//            projectDao.saveAppraisalCertInfo(appraisalCertInfo.getAppraisalCertNo());
            return Result.success("鉴证书信息添加成功");
        }else {
            return Result.failure("鉴证书信息添加失败");
        }
    }
//    获取鉴证书信息，获取基础证书模板图片
    @RequestMapping("/getBaseBook")
    public Map<String, Object> getBaseBook() {
        Map<String, Object> response = new HashMap<>();
        try {
            // 从 resources/static 中读取 book.png 文件
            ClassPathResource imgFile = new ClassPathResource("static/book.png");
            InputStream inputStream = imgFile.getInputStream();

            // 使用 ByteArrayOutputStream 来将 InputStream 转换为 byte[]
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // 将图片字节数组转换为 Base64 编码字符串
            byte[] imageBytes = outputStream.toByteArray();
            String base64Image = Base64Utils.encodeToString(imageBytes);

            // 构建返回值
            response.put("status", "success");
            response.put("data", "data:image/png;base64," + base64Image);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Failed to load image: " + e.getMessage());
        }
        return response;
    }
//    @RequestMapping("/generateAppCode")
//    public String generateAppCode(@RequestParam("projectCode") String projectCode) {
//        return noUtils.generateAppraisalCertNo("JZS", projectDao.getTypeByCode(projectCode));
//    }

    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        // 创建一个目标大小的空白图片
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType());

        // 获取 Graphics2D 对象
        Graphics2D g2d = resizedImage.createGraphics();

        // 绘制缩放后的图像
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);

        // 释放 Graphics2D 对象
        g2d.dispose();

        return resizedImage;
    }
    public static String getStringHash(String input) {
        try {
            // 获取摘要算法实例
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // 将字符串转换为字节数组
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // 将摘要字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            // 返回最终的十六进制字符串形式的摘要
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error while generating hash", e);
        }
    }

        /**
         * 掩码身份证号码，中间8位用 * 替换
         * @param idNumber 18位身份证号码
         * @return 掩码后的身份证号码
         */
        public String maskID(String idNumber) {
            // 校验身份证号长度是否为18位
            if (idNumber == null || idNumber.length() != 18) {
                throw new IllegalArgumentException("身份证号码必须为18位");
            }

            // 保留前6位和后4位，中间用8个 * 替代
            String maskedID = idNumber.substring(0, 6) + "********" + idNumber.substring(14);
            return maskedID;
        }
}
