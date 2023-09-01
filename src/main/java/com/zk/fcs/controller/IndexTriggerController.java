package com.zk.fcs.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zk.fcs.service.IndexTriggerService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Map;

@RestController
public class IndexTriggerController {

    @Autowired
    IndexTriggerService indexTriggerService;

    @PostMapping("/full-index")
    public ResponseEntity<Map<String, Object>> triggerFullIndexing(@RequestParam(value = "tenant") String tenant,
                                                                   @RequestParam(value = "bucket")String bucket) throws JsonProcessingException {
        indexTriggerService.triggerIndexing(tenant, bucket);
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}
