package com.zk.fcs.service.impl;

import com.zk.fcs.entity.FileIndex;
import com.zk.fcs.service.ElasticSearchService;
import com.zk.fcs.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class SearchServiceImpl implements SearchService {

    private final ElasticSearchService elasticSearchService;

    @Autowired
    public SearchServiceImpl(ElasticSearchService elasticSearchService) {
        this.elasticSearchService = elasticSearchService;
    }

    @Override
    public List<FileIndex> getFileUrlByTerm(String tenant, String term) throws IOException {

        List<FileIndex> fileIndexList = this.elasticSearchService.searchOnContentAndIndex("docs_" + tenant, term, FileIndex.class);
        return fileIndexList;
    }
}
