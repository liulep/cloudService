package com.yue.file.service.impl;

import com.yue.file.properties.OssProperties;
import com.yue.file.service.intf.AbstractTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

//分片上传
public class MultipartTemplateImpl extends AbstractTemplate {

    private final OssProperties ossProperties;
    public MultipartTemplateImpl(S3Client s3Client,OssProperties ossProperties) {
        super(s3Client);
        this.ossProperties = ossProperties;
    }

    @Override
    public void putObject(String bucketName, String objectName, MultipartFile file) throws IOException {
        //分片上传
        if(this.isExistBucket(bucketName)){
            byte[] bytes = file.getBytes();
            //创建获取上传ID请求
            CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                    .bucket(bucketName)
                    .key(objectName)
                    .contentType(file.getContentType())
                    .build();
            CreateMultipartUploadResponse multipartUpload = this.getS3Client().createMultipartUpload(createMultipartUploadRequest);
            //得到上传ID
            String uploadId = multipartUpload.uploadId();
            List<CompletedPart> completedParts = new LinkedList<>();
            if(this.ossProperties.getSliceSize() <= 0){
                throw new RuntimeException("平均分块大小不能少于或等于0");
            }
            int offset = 0;
            int length = this.ossProperties.getSliceSize() * 1024 * 1024;
            int num =bytes.length / length + 1;
            //进行分段上传
            for(int i=1; i <= num ; i++){
                UploadPartRequest uploadPartRequest  =  UploadPartRequest.builder()
                        .bucket(bucketName)
                        .key(objectName)
                        .uploadId(uploadId)
                        .partNumber(i)
                        .build();
                if(length >= bytes.length){
                    length = bytes.length - offset;
                }
                String etag = this.getS3Client().uploadPart(uploadPartRequest, RequestBody.fromByteBuffer(
                        ByteBuffer.wrap(bytes,offset,length))).eTag();
                CompletedPart completedPart = CompletedPart.builder()
                        .partNumber(i)
                        .eTag(etag)
                        .build();
                completedParts.add(completedPart);
            }
            CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                    .parts(completedParts)
                    .build();
            //将分段的进行合并
            this.getS3Client().completeMultipartUpload(completeMultipartUploadRequest ->
                    completeMultipartUploadRequest.bucket(bucketName)
                            .key(objectName)
                            .uploadId(uploadId)
                            .multipartUpload(completedMultipartUpload));
        }
    }

    @Override
    public ResponseBytes<GetObjectResponse> getObject(String bucketName, String objectName) {
        return null;
    }

}
