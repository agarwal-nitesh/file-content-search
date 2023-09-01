package com.zk.fcs.service;

import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface S3FileService {
    List<S3Object> getAllFiles(String tenant, String bucketName);

    void downloadFileContent(String tenant, String bucketName, String key, String localFilePath) throws FileNotFoundException, IOException;
}
