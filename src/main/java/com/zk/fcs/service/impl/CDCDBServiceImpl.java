package com.zk.fcs.service.impl;

import com.zk.fcs.entity.FileIngestionData;
import com.zk.fcs.repository.FileIngestionDataRepository;
import com.zk.fcs.service.CDCDBService;
import org.springframework.stereotype.Service;

@Service
public class CDCDBServiceImpl implements CDCDBService {

    private final FileIngestionDataRepository fileIngestionDataRepository;

    public CDCDBServiceImpl(FileIngestionDataRepository fileIngestionDataRepository) {
        this.fileIngestionDataRepository = fileIngestionDataRepository;
    }

    @Override
    public void saveProcessedFileInfo(FileIngestionData fileIngestionData) {
        this.fileIngestionDataRepository.save(fileIngestionData);
    }

    @Override
    public FileIngestionData fetchFileIngestionDataByName(String fileName) {
        return this.fileIngestionDataRepository.findByFileName(fileName);
    }
}
