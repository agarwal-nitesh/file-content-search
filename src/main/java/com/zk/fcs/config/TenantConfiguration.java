package com.zk.fcs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@ConfigurationProperties(prefix = "files-config")
public class TenantConfiguration {

    private List<Tenant> tenants;

    public List<Tenant> getTenants() {
        return tenants;
    }

    public void setTenants(List<Tenant> tenants) {
        this.tenants = tenants;
    }

    public static class Tenant {
        private String name;
        private FileResource fileResource;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public FileResource getFileResource() {
            return fileResource;
        }

        public void setFileResource(FileResource fileResource) {
            this.fileResource = fileResource;
        }

        public static class FileResource {
            private S3Config s3;

            public S3Config getS3() {
                return s3;
            }

            public void setS3(S3Config s3) {
                this.s3 = s3;
            }
        }
    }

    public static class S3Config {
        private String accessKey;
        private String secretKey;

        private Set<BucketConfig> buckets;

        private final Map<String, BucketConfig> bucketConfigMap = new HashMap<>();

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }

        public Set<BucketConfig> getBuckets() {
            return buckets;
        }

        public void setBuckets(Set<BucketConfig> buckets) {
            this.buckets = buckets;
            for (BucketConfig bucketConfig: buckets) {
                bucketConfigMap.put(bucketConfig.getName(), bucketConfig);
            }
        }

        public BucketConfig getBucketConfig(String name) {
            return bucketConfigMap.get(name);
        }
    }

    public static class BucketConfig {
        private String name;
        private String url;

        private String region;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BucketConfig that = (BucketConfig) o;
            return name.equals(that.name) && region.equals(that.region);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, region);
        }
    }
}
