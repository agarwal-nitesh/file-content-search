package com.zk.fcs.kafka.consumer;

import com.zk.fcs.kafka.producer.KafkaProducer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    private final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "${spring.kafka.topic.name.consumer}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ConsumerRecord<String, String> payload){
        log.info("topic: {}", payload.topic());
        log.info("key: {}", payload.key());
        log.info("Headers: {}", payload.headers());
        log.info("Partion: {}", payload.partition());
        log.info("Order: {}", payload.value());

    }
}
