package com.zk.fcs.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class ConfigInMemoryCache {
    private final Map<String, TenantConfiguration.Tenant> tenantConfigMap;
    private final TenantConfiguration tenantConfiguration;

    @Autowired
    public ConfigInMemoryCache(TenantConfiguration tenantConfiguration) {
        this.tenantConfigMap = new HashMap<>();
        this.tenantConfiguration = tenantConfiguration;
    }

    @PostConstruct
    private void loadConfig() {
        for (TenantConfiguration.Tenant tenant: tenantConfiguration.getTenants()) {
            tenantConfigMap.put(tenant.getName(), tenant);
        }
    }

    public TenantConfiguration.S3Config getTenantS3Config(String tenantName, String bucketName) {
        TenantConfiguration.Tenant tenant = this.tenantConfigMap.get(tenantName);
        TenantConfiguration.BucketConfig bucketConfig = new TenantConfiguration.BucketConfig();
        bucketConfig.setName(bucketName);
        if (tenant.getFileResource().getS3().getBuckets().contains(bucketConfig)) {
            return tenant.getFileResource().getS3();
        } else {
            return null;
        }
    }
}
