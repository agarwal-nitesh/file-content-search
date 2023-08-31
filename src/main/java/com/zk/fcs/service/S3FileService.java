package com.zk.fcs.service;

import com.zk.fcs.config.ConfigInMemoryCache;
import com.zk.fcs.config.TenantConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

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

    public void readFile(String fileUrl) {

    }
}
