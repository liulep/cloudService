package com.yue.common.model.entity;

import com.yue.common.constant.enums.ResultCodeEnum;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import static com.yue.common.constant.enums.ResultCodeEnum.ERROR;
import static com.yue.common.constant.enums.ResultCodeEnum.SUCCESS;

@Data
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID=123426324L;

    private Boolean success;

    private Integer code;

    private String message;

    private T data;

    private Date createTime;

    private R(){}

    public static <T> R<T> ok(){
        R<T> r = new R<T>();
        r.setSuccess(SUCCESS.getSuccess());
        r.setCode(SUCCESS.getCode());
        r.setMessage(SUCCESS.getMessage());
        r.setCreateTime(new Date());
        return r;
    }

    public static <T> R<T> error(){
        R<T> r = new R<T>();
        r.setSuccess(ERROR.getSuccess());
        r.setCode(ERROR.getCode());
        r.setMessage(ERROR.getMessage());
        r.setCreateTime(new Date());
        return r;
    }

    public static <T> R<T> ok(T t){
        R<T> r = R.ok();
        r.setData(t);
        return r;
    }

    public static <T> R<T> error(T t){
        R<T> r = R.error();
        r.setData(t);
        return r;
    }

    public static <T> R<T> setResult(ResultCodeEnum codeEnum){
        R<T> r = new R<T>();
        r.setSuccess(codeEnum.getSuccess());
        r.setMessage(codeEnum.getMessage());
        r.setCode(codeEnum.getCode());
        r.setCreateTime(new Date());
        return r;
    }

    public R<T> message(String message){
        this.message=message;
        return this;
    }

    public R<T> code(Integer code){
        this.code=code;
        return this;
    }

    public R<T> success(Boolean success){
        this.success=success;
        return this;
    }

    public R<T> data(T data){
        this.data=data;
        return this;
    }
}
