package com.zk.fcs.service;

import com.zk.fcs.entity.FileIndex;
import com.zk.fcs.entity.FileIngestionData;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.ContentHandler;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
public class IngestionService {

    private final S3FileService s3FileService;
    private final ElasticSearchService elasticSearchService;

    @Autowired
    public IngestionService(S3FileService s3FileService, ElasticSearchService elasticSearchService) {
        this.s3FileService = s3FileService;
        this.elasticSearchService = elasticSearchService;
    }

    private FileIndex readFromInputStream(InputStream inputStream, String tenant, String bucket, String fileName) throws SAXException, IOException, TikaException {
        TikaInputStream tikaInputStream = TikaInputStream.get(inputStream);
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

    public void ingestDocument(FileIngestionData fileIngestionData) throws SAXException, IOException, TikaException {

        try (InputStream inputStream = s3FileService.getFileContent(fileIngestionData.getTenant(), fileIngestionData.getFileDirectory(), fileIngestionData.getFileName())) {
            FileIndex fileIndex = readFromInputStream(inputStream, fileIngestionData.getTenant(), fileIngestionData.getFileDirectory(), fileIngestionData.getFileName());
            this.elasticSearchService.indexFileIndexDocument("docs_" + fileIngestionData.getTenant(), fileIndex);
        }
    }

    public void ingestTestDocument(String tenant, String bucket) throws SAXException, IOException, TikaException {
        ClassLoader classLoader = getClass().getClassLoader();
        String folder = "s3Test/" + tenant + "/" + bucket;
        URL url = classLoader.getResource(folder);
        String path = url.getPath();
        File[] files = new File(path).listFiles();
        // At this point put in kafka
        // skipping that while testing
        for (File file : files) {
            String fileName = file.getName();
            if (!fileName.equals("Shakespeare.txt")) {
                continue;
            }
            try (InputStream is = this.s3FileService.testReadFile(tenant, bucket, fileName)) {
                FileIndex fileIndex = readFromInputStream(is, tenant, bucket, fileName);

                this.elasticSearchService.indexFileIndexDocument("docs_" + tenant, fileIndex);
            }
        }

    }

    public String convertInputStreamToString(InputStream inputStream)
            throws IOException {

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, StandardCharsets.UTF_8))) {
            int c = 0;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        String str = textBuilder.toString();
        return str;
    }
}
