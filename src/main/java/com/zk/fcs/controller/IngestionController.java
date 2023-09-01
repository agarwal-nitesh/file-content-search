package com.zk.fcs.controller;

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
import java.util.Map;

@RestController
public class IngestionController {

    @Autowired
    private IngestionService ingestionService;

    @PostMapping("/test/ingest")
    public ResponseEntity<Map<String, Object>> TestIngestContent(@RequestParam(value = "tenant") String tenant,
                                                                 @RequestParam(value = "bucket")String bucket) throws SAXException, IOException, TikaException {
        ingestionService.ingestTestDocument(tenant, bucket);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
