package com.yue.file.service.intf;

import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface OSSTemplate {

    //创建bucket
    public void createBucket(String bucketName);

    //获取所有的bucket
    public List<Bucket> getAllBuckets();

    //删除Bucket
    public void removeBucketByName(String bucketName);

    //上传文件
    public void putObject(String bucketName, String objectName, InputStream stream) throws IOException;

    //获取文件
    public S3Object getObject(String bucketName,String objectName);

    //获取对象的URL
    public String getObjectUrl(String bucketName,String objectName,Integer expires);

    //通过BucketName和objectName删除对象
    public void removeObject(String bucketName,String objectName) throws IOException;

    //根据文件前缀查询文件
    public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName,String prefix,boolean recursive);
}
