package com.zk.fcs.service;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface IndexTriggerService {
    void triggerIndexing(String tenant, String bucketName) throws JsonProcessingException;
}
