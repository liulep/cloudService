package com.yue.route.register.service;
import com.yue.route.register.listener.RouteDeleteListener;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class RouteRegisterImportSelector implements DeferredImportSelector{
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[]{RouteRegisterBeanPostProcess.class.getName()};
    }
}
