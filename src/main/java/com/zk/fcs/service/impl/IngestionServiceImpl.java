package com.zk.fcs.service.impl;

import com.zk.fcs.entity.FileIndex;
import com.zk.fcs.entity.FileIngestionData;
import com.zk.fcs.service.CDCDBService;
import com.zk.fcs.service.ElasticSearchService;
import com.zk.fcs.service.IngestionService;
import com.zk.fcs.service.S3FileService;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class IngestionServiceImpl implements IngestionService {

    private final S3FileService s3FileService;
    private final ElasticSearchService elasticSearchService;

    private final CDCDBService cdcdbService;

    public IngestionServiceImpl(S3FileService s3FileService, ElasticSearchService elasticSearchService, CDCDBService cdcdbService) {
        this.s3FileService = s3FileService;
        this.elasticSearchService = elasticSearchService;
        this.cdcdbService = cdcdbService;
    }

    @Override
    public FileIndex readFromInputStream(InputStream inputStream, String tenant, String bucket, String fileName) throws SAXException, IOException, TikaException {
        Parser parser = new AutoDetectParser();
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        parser.parse(inputStream, handler, metadata, context);
        FileIndex fileIndex = new FileIndex();
        fileIndex.setFileContent(handler.toString());
        fileIndex.setTenant(tenant);
        fileIndex.setFileName(tenant + "_" + bucket + "_" + fileName);
        fileIndex.setMetaData(metadata.toString());
        return fileIndex;
    }

    @Override
    public void ingestDocument(FileIngestionData fileIngestionData) throws SAXException, IOException, TikaException {
        String localFilePath = "/tmp/" + fileIngestionData.getTenant() + "_" + fileIngestionData.getFileDirectory() + "_" + fileIngestionData.getFileName();
        fileIngestionData.setLastRunTs(System.currentTimeMillis());
        s3FileService.downloadFileContent(fileIngestionData.getTenant(), fileIngestionData.getFileDirectory(), fileIngestionData.getFileName(), localFilePath);
        Path filePath = Paths.get(localFilePath);
        try (InputStream inputStream = Files.newInputStream(filePath)) {
            FileIndex fileIndex = readFromInputStream(inputStream, fileIngestionData.getTenant(), fileIngestionData.getFileDirectory(), fileIngestionData.getFileName());
            this.elasticSearchService.indexFileIndexDocument("docs_" + fileIngestionData.getTenant(), fileIndex);
            cdcdbService.saveProcessedFileInfo(fileIngestionData);
        } finally {
            Files.delete(filePath);
        }
    }
}
