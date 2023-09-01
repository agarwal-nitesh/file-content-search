package com.zk.fcs.service;

import com.zk.fcs.config.ConfigInMemoryCache;
import com.zk.fcs.config.TenantConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.util.List;

@Service
public class S3FileService {

    private final ConfigInMemoryCache configInMemoryCache;

    @Autowired
    public S3FileService(ConfigInMemoryCache configInMemoryCache) {
        this.configInMemoryCache = configInMemoryCache;
    }

    public List<S3Object> getAllFiles(String tenant, String bucketName) {
        TenantConfiguration.S3Config s3Config = configInMemoryCache.getTenantS3Config(tenant, bucketName);
         if (s3Config == null) {
            return null;
        }
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey());
        TenantConfiguration.BucketConfig bucketConfig = s3Config.getBucketConfig(bucketName);
        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(bucketConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build()) {
            ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);
            return listObjectsResponse.contents();
        }
    }

    public InputStream getFileContent(String tenant, String bucketName, String key) {
        TenantConfiguration.S3Config s3Config = configInMemoryCache.getTenantS3Config(tenant, bucketName);
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(s3Config.getAccessKey(), s3Config.getSecretKey());
        try (S3Client s3Client = S3Client.builder()
                .region(Region.of(s3Config.getBucketConfig(bucketName).getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            return s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
        }
    }

    public InputStream testReadFile(String tenant, String bucketName, String fileName) {

//        InputStream inputStream = classLoader.getResourceAsStream();
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream("s3Test/" + tenant + "/" + bucketName + "/" + fileName);
    }
}
