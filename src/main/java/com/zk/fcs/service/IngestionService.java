package com.zk.fcs.service;

import com.zk.fcs.entity.FileIndex;
import com.zk.fcs.entity.FileIngestionData;
import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public interface IngestionService {
    FileIndex readFromInputStream(InputStream inputStream, String tenant, String bucket, String fileName) throws SAXException, IOException, TikaException;

    void ingestDocument(FileIngestionData fileIngestionData) throws SAXException, IOException, TikaException;
}
