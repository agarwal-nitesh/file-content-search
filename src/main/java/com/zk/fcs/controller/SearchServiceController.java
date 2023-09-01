package com.zk.fcs.controller;

import com.zk.fcs.entity.FileIndex;
import com.zk.fcs.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class SearchServiceController {

    @Autowired
    private SearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<FileIndex>> searchContent(@RequestParam(value = "tenant") String tenant,
                                                         @RequestParam(value = "term")String term) throws IOException {
        List<FileIndex> fileIndexList = searchService.getFileUrlByTerm(tenant, term);
        return new ResponseEntity<>(fileIndexList, HttpStatus.OK);
    }
}
