package com.zk.fcs.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

@Entity
@Table(name = "file_ingestion_data")
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileIngestionData {
    public static enum FileSource {S3};
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String fileName;

    private String fileDirectory;
    @Enumerated(EnumType.STRING)
    private FileSource source;
    private long lastRunTs; // In millis
    private String tenant;

    private String tag;

    private long size;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public FileSource getSource() {
        return source;
    }

    public void setSource(FileSource source) {
        this.source = source;
    }

    public long getLastRunTs() {
        return lastRunTs;
    }

    public void setLastRunTs(long lastRunTs) {
        this.lastRunTs = lastRunTs;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getFileDirectory() {
        return fileDirectory;
    }

    public void setFileDirectory(String fileDirectory) {
        this.fileDirectory = fileDirectory;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
