package com.blockexplore.controller;

import com.blockexplore.utils.ImageBase64Utils;
import com.blockexplore.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@Slf4j
@RequestMapping("/base64")
public class Base64UtilsController {

    @PostMapping("/toBase64")
    public Result<String> imgToBase64(@RequestBody MultipartFile file) throws IOException {
        log.info("将图片转base64：{}",file);
        String s = ImageBase64Utils.imageToBase64(file);
        return Result.success(s);
    }

}
