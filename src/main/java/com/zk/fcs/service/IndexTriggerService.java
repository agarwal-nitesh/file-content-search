package com.zk.fcs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.fcs.entity.FileIngestionData;
import com.zk.fcs.kafka.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;

@Service
public class IndexTriggerService {
    private final S3FileService s3FileService;
    private final KafkaProducer kafkaProducer;

    private final ObjectMapper objectMapper;

    private final String fileIndexerTopic;

    private final CDCDBService cdcdbService;

    @Autowired
    public IndexTriggerService(S3FileService s3FileService, KafkaProducer kafkaProducer,
                               @Value("${spring.kafka.topic.name.producer}") String producerTopic,
                               ObjectMapper objectMapper, CDCDBService cdcdbService) {
        this.s3FileService = s3FileService;
        this.kafkaProducer = kafkaProducer;
        this.fileIndexerTopic = producerTopic;
        this.objectMapper = objectMapper;
        this.cdcdbService = cdcdbService;
    }

    public void triggerIndexing(String tenant, String bucketName) throws JsonProcessingException {
        List<S3Object> s3Objects = s3FileService.getAllFiles(tenant, bucketName);
        for (S3Object s3Object: s3Objects) {
            FileIngestionData fileIngestionData = new FileIngestionData();
            fileIngestionData.setFileName(s3Object.key());
            fileIngestionData.setFileDirectory(bucketName);
            fileIngestionData.setSource(FileIngestionData.FileSource.S3);
            fileIngestionData.setTenant(tenant);
            fileIngestionData.setTag(s3Object.eTag());
            fileIngestionData.setSize(s3Object.size());
            FileIngestionData lastProcessedData = cdcdbService.fetchFileIngestionDataByName(fileIngestionData.getFileName());
            if (lastProcessedData != null && lastProcessedData.getLastRunTs() > s3Object.lastModified().toEpochMilli()) {
                return;
            }
            kafkaProducer.send(fileIndexerTopic, tenant + "_" + bucketName + "_" + fileIngestionData.getFileName(), this.objectMapper.writeValueAsString(fileIngestionData));
        }
    }
}
