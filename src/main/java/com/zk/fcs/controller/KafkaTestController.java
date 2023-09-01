package com.zk.fcs.controller;

import com.zk.fcs.entity.FileIndex;
import com.zk.fcs.kafka.producer.KafkaProducer;
import com.zk.fcs.service.SearchService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

@RestController
public class KafkaTestController {
    @Autowired
    private KafkaProducer kafkaProducer;

    @GetMapping("/kafka/produce")
    public ResponseEntity<List<FileIndex>> TestIngestContent(@RequestParam(value = "topic") String topic,
                                                             @RequestParam(value = "key")String key,
                                                             @RequestParam(value = "message")String message) {
        kafkaProducer.send(topic, key, message);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
