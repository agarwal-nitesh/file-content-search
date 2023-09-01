package com.zk.fcs.service;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;

public interface AdminService {
    void ingestTestDocument(String tenant, String bucket) throws SAXException, IOException, TikaException;
    InputStream testReadFile(String tenant, String bucketName, String fileName);
}
