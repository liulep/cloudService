package com.yue.common.model.entity;

import com.yue.common.constant.enums.ResultCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class YueException extends RuntimeException {

    private Integer code;

    private String message;

    public YueException(ResultCodeEnum resultCodeEnum){
        this.code=resultCodeEnum.getCode();
        this.message=resultCodeEnum.getMessage();
    }

    public YueException(Integer code, String message){
        this.code=code;
        this.message=message;
    }

    public YueException(String message){
        this.code=400;
        this.message=message;
    }
}
