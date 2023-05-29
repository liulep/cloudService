package com.yue.resp.handler;

import com.yue.common.model.entity.Error;
import com.yue.common.model.entity.YueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 定义全局异常捕捉
@RestControllerAdvice(value = "com.yue")
public class YueExceptionHandler {

    //捕捉自定义的异常
    @ExceptionHandler(YueException.class)
    public Error yueException(YueException exception){
        return Error.builder()
                .message(exception.getMessage())
                .code(exception.getCode())
                .build();
    }
}
