package com.yue.file.service.impl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.yue.file.service.intf.OSSTemplate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class OSSTemplateImpl implements OSSTemplate {

    private final AmazonS3 amazonS3;

    public OSSTemplateImpl(AmazonS3 amazonS3){
        this.amazonS3=amazonS3;
    }

    @Override
    public void createBucket(String bucketName) {
        boolean b = this.amazonS3.doesBucketExistV2(bucketName);
        if(!b){
            this.amazonS3.createBucket(bucketName);
        }
    }

    @Override
    public List<Bucket> getAllBuckets() {
        return amazonS3.listBuckets();
    }

    @Override
    public void removeBucketByName(String bucketName) {
        boolean b = this.amazonS3.doesBucketExistV2(bucketName);
        if(b){
            this.amazonS3.deleteBucket(bucketName);
        }
    }

    @Override
    public void putObject(String bucketName, String objectName, InputStream stream) throws IOException {
        boolean b = this.amazonS3.doesBucketExistV2(bucketName);
        if(b){
            //application/octet-stream是应用程序文件的默认值。意思是未知的应用程序文件 ，
            // 浏览器一般不会自动执行或询问执行。浏览器会像对待，
            // 设置了HTTP头Content-Disposition 值为 attachment 的文件一样来对待这类文件，
            // 即浏览器会触发下载行为。
           this.putObject(bucketName,objectName,stream,stream.available(),"application/octet-stream");
        }
    }

    @Override
    public S3Object getObject(String bucketName, String objectName) {
        boolean b = this.amazonS3.doesBucketExistV2(bucketName);
        S3Object object = null;
        if(b){
            object = this.amazonS3.getObject(bucketName, objectName);
        }
        return object;
    }

    @Override
    public String getObjectUrl(String bucketName, String objectName, Integer expires) {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,expires);
        URL url = amazonS3.generatePresignedUrl(bucketName, objectName, calendar.getTime());
        return url.toString();
    }

    @Override
    public void removeObject(String bucketName, String objectName) throws IOException {
        S3Object object = this.amazonS3.getObject(bucketName, objectName);
        if(object != null){
            this.amazonS3.deleteObject(bucketName,objectName);
        }
    }

    @Override
    public List<S3ObjectSummary> getAllObjectsByPrefix(String bucketName, String prefix, boolean recursive) {
        ObjectListing objectListing = this.amazonS3.listObjects(bucketName, prefix);
        return objectListing.getObjectSummaries();
    }

    //上传文件
    private void putObject(String bucketName, String objectName, InputStream stream, long size, String contentType) throws IOException {
        byte[] bytes = stream.readAllBytes();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(size);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        //开始上传
        this.amazonS3.putObject(bucketName, objectName, byteArrayInputStream, objectMetadata);
    }
}
