package com.yue.feign.intercept;
import com.yue.common.constant.constnt.TokenConstant;
import com.yue.common.utils.RequestContextUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

// 解决feign中令牌中继问题
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        HttpServletRequest request = RequestContextUtils.getRequest();
        Map<String, String> headers = getHeaders(request);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestTemplate.header(entry.getKey(), entry.getValue());
        }
    }

    //获取原请求头
    private Map<String,String> getHeaders(HttpServletRequest request){
        Map<String,String> map = new LinkedHashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if(headerNames != null){
            while(headerNames.hasMoreElements()){
                String key = headerNames.nextElement();
                String value= request.getHeader(key);
                //转递令牌信息
                if(TokenConstant.TOKEN_NAME.equals(key)){
                    map.put(key,value);
                    break;
                }
                //转递灰度标记信息
            }
        }
        return map;
    }
}
