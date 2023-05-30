package com.yue.commit.service;

import com.yue.commit.anno.AvoidRepeatableCommit;
import com.yue.common.constant.constnt.KeyConstant;
import com.yue.common.model.entity.YueException;
import com.yue.common.utils.IpUtils;
import com.yue.common.utils.RequestContextUtils;
import com.yue.common.utils.id.SnowFlakeFactory;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class AvoidRepeatableCommitAspect {

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    @Around("@annotation(com.yue.commit.anno.AvoidRepeatableCommit)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        //获取request
        HttpServletRequest request = RequestContextUtils.getRequest();
        //获取ip地址
        String ip = IpUtils.getIpAddress(request);
        //获取注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        //目标类
        String className=method.getDeclaringClass().getName();
        //目标方法
        String methodName = method.getName();
        //得到类加方法
        String ipKey = String.format("%s#%s", className, methodName);
        //得到hashcode
        int hashcode = Math.abs(ipKey.hashCode());
        String key = String.format("%s:%s_%d", KeyConstant.AVOID_REPEATABLE_COMMIT, ip, hashcode);
        //key = yue-service:avoid_repeatable_commit_hashcode
        AvoidRepeatableCommit annotation = method.getAnnotation(AvoidRepeatableCommit.class);
        long timeout = annotation.timeout();
        //设置过期时间,采用setNx互斥操作
        Boolean flag = redisTemplate.opsForValue().setIfAbsent(key, SnowFlakeFactory.getSnowFlake().nextId(), timeout, TimeUnit.MILLISECONDS);
        //如果已经存在
        if(Boolean.FALSE.equals(flag)){
            throw new YueException("请勿重复提交");
        }
        return point.proceed();
    }
}
