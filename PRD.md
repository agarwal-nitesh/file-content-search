# Product Requirement Document (PRD)

## Context

Build an application that searches file content from cloud storage.

## Goals & Key Performance Indicators

1. For a particular customer (tenant), we should be able to search a text token (word) and find all the files that contain this token.
2. The search should be fast.

## Constraints

1. The size of the documents will be less than say 200 MB
2. We will only use the S3 source of our tenants
3. The application will not schedule full indexing or sync.

## Assumptions

1. A multi-tenant system
2. Content searched will always be for a particular tenant
3. No ES proxy for rate limits

## Dependencies

1. An inverted-index data structure to convert content into tokens and index tokens
2. A change data pipeline that captures the last processed time
3. A queue to make sure that file content download and ingestion can be resilient


## Enhancements

1. Store tenant configurations in the database
2. Encrypt tenant configuration by using Jasypt, store in db with pgcrypt, or use a vault
3. Add rate-limiting to ES client
4. For S3 source use S3 Select to stream efficiently
5. Explore Apache Tika 2.5
6. Add integration tests using Embedded SQL (H2), Embedded Kafka, Embedded ES, or a docker container-based integration test
