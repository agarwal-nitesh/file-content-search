package com.zk.fcs.service;

import com.zk.fcs.entity.FileIngestionData;
import com.zk.fcs.repository.FileIngestionDataRepository;
import org.springframework.stereotype.Service;

@Service
public class CDCDBService {

    private final FileIngestionDataRepository fileIngestionDataRepository;

    public CDCDBService(FileIngestionDataRepository fileIngestionDataRepository) {
        this.fileIngestionDataRepository = fileIngestionDataRepository;
    }

    public void saveProcessedFileInfo(FileIngestionData fileIngestionData) {
        this.fileIngestionDataRepository.save(fileIngestionData);
    }

    public FileIngestionData fetchFileIngestionDataByName(String fileName) {
        return this.fileIngestionDataRepository.findByFileName(fileName);
    }
}
