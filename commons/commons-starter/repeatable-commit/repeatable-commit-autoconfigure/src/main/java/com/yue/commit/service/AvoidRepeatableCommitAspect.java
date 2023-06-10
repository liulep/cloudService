package com.yue.commit.service;

import com.alibaba.fastjson2.JSON;
import com.yue.commit.anno.AvoidRepeatableCommit;
import com.yue.commit.utils.ReqDeduplicationHelper;
import com.yue.common.constant.constnt.KeyConstant;
import com.yue.common.model.entity.YueException;
import com.yue.common.model.security.LoginUser;
import com.yue.common.utils.IpUtils;
import com.yue.common.utils.OauthUtils;
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
        //获取已登录用户的ID
        LoginUser user = OauthUtils.getCurrentUser();
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
        String ipKey = String.format("%s#%s#%s", ip, className, methodName);
        if(point.getArgs()!= null && point.getArgs().length != 0){
            //获取参数
            String args = new ReqDeduplicationHelper().deduplicationParamMD5(JSON.toJSONString(point.getArgs()[0]));
            ipKey = String.format("%s#%s#%s#%s", ip, className, methodName, args);
        }
        //得到hashcode
        int hashcode = Math.abs(ipKey.hashCode());
        String key = String.format("%s:uid_%s", KeyConstant.AVOID_REPEATABLE_COMMIT, hashcode);
        //key = yue-service:uid_7217867821921985
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
