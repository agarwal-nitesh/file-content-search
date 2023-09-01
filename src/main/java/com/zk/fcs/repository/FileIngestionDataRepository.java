package com.zk.fcs.repository;

import com.zk.fcs.entity.FileIngestionData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileIngestionDataRepository extends JpaRepository<FileIngestionData, Integer> {
    FileIngestionData findByFileName(String fileName);
}
