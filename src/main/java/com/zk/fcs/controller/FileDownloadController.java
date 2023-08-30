package com.zk.fcs.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FileDownloadController {
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> customHealth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
    }
}
