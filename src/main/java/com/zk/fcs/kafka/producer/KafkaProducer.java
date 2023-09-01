package com.zk.fcs.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {
   private final Logger log = LoggerFactory.getLogger(KafkaProducer.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, String messageKey, String message){
        log.info("Payload: topic = {}, key = {}, message = {}", topic, messageKey, message);
        kafkaTemplate.send(topic, messageKey, message);
    }
}
