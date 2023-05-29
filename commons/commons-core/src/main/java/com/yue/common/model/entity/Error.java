package com.yue.common.model.entity;

import com.yue.common.constant.enums.ResultCodeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Error { //定义错误信息

    private Integer code; // 状态码

    private String message; //错误信息

    public static Error fail(ResultCodeEnum codeEnum,Throwable e,String message){
        Error error = Error.fail(codeEnum, e);
        error.setMessage(message);
        return error;
    }

    public static Error fail(ResultCodeEnum codeEnum,Throwable e){
        Error error = new Error();
        error.setCode(codeEnum.getCode());
        error.setMessage(codeEnum.getMessage());
        return error;
    }
}
