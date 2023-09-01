package com.zk.fcs.service;

import com.zk.fcs.config.ConfigInMemoryCache;
import com.zk.fcs.config.TenantConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class S3FileService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
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

    public void downloadFileContent(String tenant, String bucketName, String key, String localFilePath) throws FileNotFoundException, IOException {
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
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
            try (FileOutputStream fileOutputStream = new FileOutputStream(localFilePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = responseInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, bytesRead);
                }
            }

            log.info("File downloaded and saved to: " + localFilePath);
        }
    }

    public InputStream testReadFile(String tenant, String bucketName, String fileName) {

//        InputStream inputStream = classLoader.getResourceAsStream();
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream("s3Test/" + tenant + "/" + bucketName + "/" + fileName);
    }

    public String convertInputStreamToString(InputStream inputStream)
            throws IOException {

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        String str = textBuilder.toString();
        return str;
    }
}
