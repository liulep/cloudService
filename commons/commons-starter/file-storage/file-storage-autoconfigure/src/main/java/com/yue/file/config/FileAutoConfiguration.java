package com.yue.file.config;
import com.yue.file.properties.OssProperties;
import com.yue.file.service.impl.FileTemplateImpl;
import com.yue.file.service.impl.MultipartTemplateImpl;
import com.yue.file.service.intf.AbstractTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
@EnableConfigurationProperties(OssProperties.class)
public class FileAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client(OssProperties ossProperties){
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(ossProperties.getAccessKey(), ossProperties.getSecretKey())))
                .region(Region.AWS_CN_GLOBAL)
                .endpointOverride(URI.create(ossProperties.getEndpoint()))
                .serviceConfiguration(S3Configuration.builder()
                        .chunkedEncodingEnabled(ossProperties.getChunkedEncoding())
                        .pathStyleAccessEnabled(ossProperties.getPathStyleAccess())
                        .build())
                .build();
    }

    @Bean
    @ConditionalOnBean(S3Client.class)
    public AbstractTemplate fileTemplate(S3Client s3Client){
        return new FileTemplateImpl(s3Client);
    }

    @Bean
    @ConditionalOnBean(S3Client.class)
    public AbstractTemplate multipartTemplate(S3Client s3Client,OssProperties ossProperties){
        return new MultipartTemplateImpl(s3Client,ossProperties);
    }
}
