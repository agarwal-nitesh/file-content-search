package com.zk.fcs.service;

import com.zk.fcs.entity.FileIndex;

import java.io.IOException;
import java.util.List;

public interface ElasticSearchService {
    void indexFileIndexDocument(String index, FileIndex document) throws IOException;
    <T> List<T> searchOnContentAndIndex(String index, String term, Class<T> tClass) throws IOException;
    void deleteAll(String index) throws IOException;
}
