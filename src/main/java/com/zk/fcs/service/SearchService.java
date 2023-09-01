package com.zk.fcs.service;

import com.zk.fcs.entity.FileIndex;

import java.io.IOException;
import java.util.List;

public interface SearchService {
    List<FileIndex> getFileUrlByTerm(String tenant, String term) throws IOException;
}
