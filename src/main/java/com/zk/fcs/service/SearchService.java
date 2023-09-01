package com.zk.fcs.service;

import com.zk.fcs.entity.FileIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SearchService {

    private final ElasticSearchService elasticSearchService;

    @Autowired
    public SearchService(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    public List<FileIndex> getFileUrlByTerm(String tenant, String term) throws IOException {

        List<FileIndex> fileIndexList = this.elasticSearchService.searchOnContentAndIndex("docs_" + tenant, term, FileIndex.class);
        return fileIndexList;
    }
}
