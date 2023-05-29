package com.yue.resource.handler;

import com.alibaba.fastjson2.JSON;
import com.yue.common.model.entity.R;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.yue.common.constant.enums.ResultCodeEnum.INVALID_TOKEN;

//网关全局异常捕获
public class GlobalErrorExceptionHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        if(response.isCommitted()){
            return Mono.error(ex);
        }
        R error = R.error().code(INVALID_TOKEN.getCode())
                .message(INVALID_TOKEN.getMessage())
                .success(INVALID_TOKEN.getSuccess());
        //JSON格式返回
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        //判断异常
        if(ex instanceof ResponseStatusException rex){
            error.message(rex.getMessage());
        }
        else if(ex instanceof InvalidBearerTokenException iex){
            error.message(INVALID_TOKEN.getMessage());
        }else if(ex instanceof BadJwtException){
            error.message("token解析失败");
        }
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory dataBufferFactory = response.bufferFactory();
            try {
                return dataBufferFactory.wrap(JSON.toJSONBytes(error));
            }catch (Exception e){
                return dataBufferFactory.wrap(new byte[0]);
            }
        }));
    }
}
