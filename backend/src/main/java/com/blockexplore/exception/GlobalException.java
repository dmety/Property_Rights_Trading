package com.blockexplore.exception;

import com.blockexplore.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public Result<String> handlerException(Exception e){
//        全局异常处理
        e.printStackTrace();
        return Result.failure(StringUtils.hasLength(e.getMessage())?e.getMessage():"操作失败,请联系管理员!");
    }
}
