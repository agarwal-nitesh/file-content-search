package com.zk.fcs.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zk.fcs.entity.FileIngestionData;
import com.zk.fcs.kafka.producer.KafkaProducer;
import com.zk.fcs.service.IngestionService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);
    private final IngestionService ingestionService;
    private final ObjectMapper objectMapper;

    @Autowired
    public KafkaConsumer(IngestionService ingestionService, ObjectMapper objectMapper) {
        this.ingestionService = ingestionService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${spring.kafka.topic.name.consumer}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> payload, Acknowledgment acknowledgment){
        log.info("Payload: topic = {}, key = {}, header = {}, partition = {}, message = {}",
                payload.topic(), payload.key(), payload.headers(), payload.partition(), payload.value());
        try {
            FileIngestionData fileIngestionData = objectMapper.readValue(payload.value(), FileIngestionData.class);
            this.ingestionService.ingestDocument(fileIngestionData);

        } catch (Exception ex) {
            log.error("Error parsing data or ingesting data", ex);
            // retry and insert in DLQ

        }
        acknowledgment.acknowledge();
    }
}
