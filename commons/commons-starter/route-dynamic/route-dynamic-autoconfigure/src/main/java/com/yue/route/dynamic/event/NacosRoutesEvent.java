package com.yue.route.dynamic.event;

import org.springframework.context.ApplicationEvent;

public class NacosRoutesEvent extends ApplicationEvent {

    private String dataId;
    private String group;
    public NacosRoutesEvent(Object source) {
        super(source);
    }

    public NacosRoutesEvent(Object source,String dataId,String group){
        this(source);
        this.dataId=dataId;
        this.group=group;
    }

    public String getDataId(){
        return this.dataId;
    }

    public String getGroup(){
        return this.group;
    }

}
