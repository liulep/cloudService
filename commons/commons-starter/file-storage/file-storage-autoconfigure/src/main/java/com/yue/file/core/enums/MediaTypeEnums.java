package com.yue.file.core.enums;

import org.springframework.http.MediaType;

public enum MediaTypeEnums {

    VIDEO_MP3("video/mp3"),
    VIDEO_MP4("video/mp4");

    private final String name;

    private final MediaType mediaType;

    private MediaTypeEnums(String name){
        this.name = name;
        this.mediaType = MediaType.valueOf(name);
    }

    public String getName(){
        return this.name;
    }

    public MediaType getMediaType(){
        return this.mediaType;
    }
}
