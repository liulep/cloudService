package com.yue.file.service.intf;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;

public abstract class AbstractTemplate {

    private final S3Client s3Client;

    public AbstractTemplate(S3Client s3Client){
        this.s3Client = s3Client;
    }

    //是否存在令牌桶
    public boolean isExistBucket(String bucketName) {
        try {
            HeadBucketResponse headBucketResponse = this.s3Client
                    .headBucket(head ->
                            head.bucket(bucketName)
                    );
            return true;
        }catch (NoSuchBucketException e){
            return false;
        }
    }

    //文件是否存在
    public boolean isExistObject(String bucketName, String objectName) {
        try {
            if(this.isExistBucket(bucketName)){
                HeadObjectResponse headObjectResponse = this.s3Client.headObject(headObject ->
                        headObject.bucket(bucketName)
                                .key(objectName)
                );
                return true;
            }
            return false;
        }catch (NoSuchBucketException e){
            return false;
        }
    }

    //创建bucket
    public void createBucket(String bucketName) {
        if(!this.isExistBucket(bucketName)){
            this.s3Client.createBucket(createBucket ->
                    createBucket.acl(BucketCannedACL.PUBLIC_READ)
                            .bucket(bucketName)
            );
        }
    }

    //获取所有的bucket
    public List<Bucket> getAllBuckets() {
        ListBucketsResponse listBucketsResponse = this.s3Client.listBuckets();
        return listBucketsResponse.buckets();
    }

    //删除Bucket
    public void removeBucketByName(String bucketName) {
        if(this.isExistBucket(bucketName)){
            this.s3Client.deleteBucket(deleteBucket ->
                    deleteBucket.bucket(bucketName)
            );
        }
    }

    //通过BucketName和objectName删除对象
    public void removeObject(String bucketName, String objectName) throws IOException {
        if(this.isExistObject(bucketName,objectName)){
            this.s3Client.deleteObject(deleteObject ->
                    deleteObject.bucket(bucketName)
                            .key(objectName)
            );
        }
    }

    //根据文件前缀查询文件
    public List<S3Object> getAllObjectsByPrefix(String bucketName, String prefix) {
        if(this.isExistBucket(bucketName)){
            return this.s3Client.listObjects(listObjects ->
                    listObjects.prefix(prefix)
                            .bucket(bucketName)
            ).contents();
        }
        return null;
    }

    //获取桶全部对象
    public List<S3Object> getAllObjects(String buckName) {
        if(this.isExistBucket(buckName)){
            return this.s3Client.listObjects(listObjects ->
                    listObjects.bucket(buckName)
            ).contents();
        }
        return null;
    }

    //获取对象的URL
    public String getObjectUrl(String bucketName, String objectName) {
        if(this.isExistObject(bucketName,objectName)){
            URL url = this.getS3Client().utilities().getUrl(getObjectUrl ->
                    getObjectUrl.bucket(bucketName)
                            .key(objectName)
            );
            return url.toString();
        }
        return null;
    }

    public S3Client getS3Client(){
        return this.s3Client;
    }

    //上传文件
    public abstract void putObject(String bucketName, String objectName, MultipartFile file) throws IOException;

    //获取文件
    public abstract ResponseBytes<GetObjectResponse> getObject(String bucketName, String objectName);
}
