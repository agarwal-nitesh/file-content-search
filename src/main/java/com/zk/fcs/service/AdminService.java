package com.zk.fcs.service;

import com.zk.fcs.entity.FileIndex;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class AdminService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final S3FileService s3FileService;
    private final ElasticSearchService elasticSearchService;

    private final IngestionService ingestionService;

    public AdminService(S3FileService s3FileService, ElasticSearchService elasticSearchService, IngestionService ingestionService) {
        this.s3FileService = s3FileService;
        this.elasticSearchService = elasticSearchService;
        this.ingestionService = ingestionService;
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
            try (InputStream is = this.testReadFile(tenant, bucket, fileName)) {
                FileIndex fileIndex = this.ingestionService.readFromInputStream(is, tenant, bucket, fileName);

                this.elasticSearchService.indexFileIndexDocument("docs_" + tenant, fileIndex);
            }
        }

    }

    public InputStream testReadFile(String tenant, String bucketName, String fileName) {

//        InputStream inputStream = classLoader.getResourceAsStream();
        ClassLoader classLoader = getClass().getClassLoader();
        return classLoader.getResourceAsStream("s3Test/" + tenant + "/" + bucketName + "/" + fileName);
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
