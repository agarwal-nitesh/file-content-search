package com.zk.fcs.service;

import com.zk.fcs.entity.FileIngestionData;

public interface CDCDBService {
    void saveProcessedFileInfo(FileIngestionData fileIngestionData);

    FileIngestionData fetchFileIngestionDataByName(String fileName);
}
