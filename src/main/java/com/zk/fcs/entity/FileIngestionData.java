package com.zk.fcs.entity;

import javax.persistence.*;

@Entity
@Table(name = "file_ingestion_data")
public class FileIngestionData {
    public static enum FileSource {S3};
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileUrl;
    @Enumerated(EnumType.STRING)
    private FileSource source;
    private long lastRunTs; // In millis
    private String tenant;
}
