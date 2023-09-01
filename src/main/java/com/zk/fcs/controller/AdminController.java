package com.zk.fcs.controller;

import com.zk.fcs.entity.FileIndex;
import com.zk.fcs.kafka.producer.KafkaProducer;
import com.zk.fcs.service.ElasticSearchService;
import com.zk.fcs.service.IngestionService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {
    @Autowired
    private IngestionService ingestionService;
    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private ElasticSearchService elasticSearchService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> customHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }

    @PostMapping("/test/es/ingest")
    public ResponseEntity<Map<String, Object>> TestIngestContent(@RequestParam(value = "tenant") String tenant,
                                                                 @RequestParam(value = "bucket")String bucket) throws SAXException, IOException, TikaException {
        ingestionService.ingestTestDocument(tenant, bucket);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/test/kafka/produce")
    public ResponseEntity<List<FileIndex>> TestIngestContent(@RequestParam(value = "topic") String topic,
                                                             @RequestParam(value = "key")String key,
                                                             @RequestParam(value = "message")String message) {
        kafkaProducer.send(topic, key, message);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @GetMapping("/test/es/deleteAll")
    public ResponseEntity<List<FileIndex>> TestDeleteAllDocs(@RequestParam(value = "index") String index) throws IOException {
        elasticSearchService.deleteAll(index);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
