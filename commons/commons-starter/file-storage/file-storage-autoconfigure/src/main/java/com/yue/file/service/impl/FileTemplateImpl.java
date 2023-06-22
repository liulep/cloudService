package com.yue.file.service.impl;
import com.yue.file.service.intf.AbstractTemplate;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//文件上传实现类
public class FileTemplateImpl extends AbstractTemplate {

    public FileTemplateImpl(S3Client s3Client){
        super(s3Client);
    }

    @Override
    public void putObject(String bucketName, String objectName, MultipartFile file) throws IOException {
        if(this.isExistBucket(bucketName)){
            this.getS3Client().putObject(putObject ->
                    putObject.bucket(bucketName)
                            .key(objectName)
                            .contentType(file.getContentType())
                            .contentLength(file.getSize())
                            .contentEncoding(StandardCharsets.UTF_8.name()),
                    RequestBody.fromBytes(file.getBytes()));
        }
    }

    @Override
    public ResponseBytes<GetObjectResponse> getObject(String bucketName, String objectName) {
        if(this.isExistObject(bucketName,objectName)){
            return this.getS3Client().getObjectAsBytes(getObject ->
                    getObject.bucket(bucketName)
                            .key(objectName));
        }
       return null;
    }

}
