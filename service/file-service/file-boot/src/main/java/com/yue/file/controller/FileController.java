package com.yue.file.controller;
import com.alibaba.fastjson2.JSON;
import com.yue.common.model.entity.YueException;
import com.yue.file.core.constants.MediaTypeConstant;
import com.yue.file.core.properties.FileSuffixProperties;
import com.yue.file.properties.OssProperties;
import com.yue.file.service.intf.AbstractTemplate;
import com.yue.resp.anno.NotControllerResponseAdvice;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class FileController {

    private final AbstractTemplate fileTemplate;
    private final FileSuffixProperties fileSuffixProperties;
    private final OssProperties ossProperties;
    private final AbstractTemplate multipartTemplate;

    //上传文件
    @PostMapping("/{bucketName}/upload")
    public List<String> upload(@PathVariable(value = "bucketName")String bucketName , @RequestPart(value = "files") MultipartFile[] files){
        //创建令牌桶
        String name = this.getBucketName(bucketName);
        this.fileTemplate.createBucket(name);
        return Arrays.stream(files).filter(Objects::nonNull).map(file -> {
            String newFilename = this.uploadFile(file);
            try {
                if(this.ossProperties.getSlice()){
                    if(this.ossProperties.getFileMaxSize() < 100){
                        throw new RuntimeException("若使用切片上传文件大小至少在100MB以上!");
                    }else if(this.ossProperties.getFileMaxSize() < this.ossProperties.getSliceSize()){
                        throw new RuntimeException("文件大小不能少于切片大小");
                    }else{
                        //文件切片上传
                        this.multipartTemplate.putObject(name,newFilename,file);
                    }
                }
                else {
                    //普通文件上传
                    this.fileTemplate.putObject(name, newFilename, file);
                }
                String type = MediaTypeConstant.IMAGES_TYPE;
                if(Objects.requireNonNull(file.getContentType()).startsWith(MediaTypeConstant.IMAGE)){
                    type = MediaTypeConstant.IMAGES_TYPE;
                }else if(Objects.requireNonNull(file.getContentType()).startsWith(MediaTypeConstant.VIDEO)){
                    type = MediaTypeConstant.VIDEOS_TYPE;
                }
                return this.fileSuffixProperties.getUrlPrefix()+bucketName+type+newFilename;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }

    //流式观看视频
    @GetMapping("/{bucketName}/videos/{objectName}")
    @NotControllerResponseAdvice
    public Mono<ResponseEntity<byte[]>> videos(@PathVariable(value = "bucketName")String bucketName,@PathVariable(value = "objectName")String objectName){
        String name = this.getBucketName(bucketName);
        ResponseBytes<GetObjectResponse> objects = this.fileTemplate.getObject(name, objectName);
        return Mono.just(ResponseEntity.ok()
                .contentType(MediaType.valueOf(objects.response().contentType()))
                .contentLength(objects.asByteArray().length)
                .body(objects.asByteArray()));
    }

    //在线预览图片
    @GetMapping("/{bucketName}/images/{objectName}")
    @NotControllerResponseAdvice
    public ResponseEntity<byte[]> images(@PathVariable(value = "bucketName")String bucketName,@PathVariable(value = "objectName")String objectName){
        String name = this.getBucketName(bucketName);
        ResponseBytes<GetObjectResponse> objects = this.fileTemplate.getObject(name, objectName);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(objects.response().contentType()))
                .contentLength(objects.asByteArray().length)
                .body(objects.asByteArray());
    }

    //下载文件
    @GetMapping("/{bucketName}/download/{objectName}")
    @NotControllerResponseAdvice
    public ResponseEntity<byte[]> download(@PathVariable(value = "bucketName") String bucketName, @PathVariable(value = "objectName")String objectName)  {
       String name = this.getBucketName(bucketName);
        ResponseBytes<GetObjectResponse> objects = this.fileTemplate.getObject(name, objectName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment;filename="
                        + URLEncoder.encode(objectName, StandardCharsets.UTF_8))
                .contentLength(objects.asByteArray().length)
                .body(objects.asByteArray());
    }

    //删除文件
    @PostMapping("/{bucketName}/delete")
    public String delete(@PathVariable(value = "bucketName")String bucketName,@RequestParam(value = "files")String[] files){
        String name = this.getBucketName(bucketName);
        Arrays.stream(files)
                .forEach(file -> {
                    try {
                        this.fileTemplate.removeObject(bucketName,file);
                    } catch (IOException e) {
                        throw new YueException("删除失败，可能的错误：文件不存在或者存储桶不存在");
                    }
                });
        return "删除成功";
    }

    public String uploadFile(MultipartFile file){
        if(file!=null){
            String filename = file.getOriginalFilename();
            String suffix = filename.substring(filename.lastIndexOf(".")+1);
            suffix=suffix.toLowerCase();
            if(!this.fileSuffixProperties.getIncludes().contains(suffix)){
                throw new YueException("不支持的文件,只支持:"+ JSON.toJSONString(fileSuffixProperties.getIncludes()));
            }
            //获取当前时间
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
            return UUID.randomUUID() +"."+suffix;
        }
        return "";
    }

    public String getBucketName(String bucketName){
        if(StringUtils.isNotBlank(ossProperties.getAppId())){
            return bucketName + "-" +ossProperties.getAppId();
        }
        return bucketName;
    }
}
