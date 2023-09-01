# Product Requirement Document (PRD)

## Context

Build an application that searches file content from cloud storage.

## Goals & Key Performance Indicators

1. For a particular customer (tenant), we should be able to search a text token (word) and find all the files that contain this token.
2. The search should be fast.

## Constraints

1. The size of the documents will be less than say 200 MB
2. We will only use the S3 source of our tenants
3. The application will not be responsible for scheduling full indexing or sync.

## Assumptions

1. A multi-tenant system
2. Content searched will always be for a particular tenant
3. No ES proxy for rate limits

## Dependencies

1. An inverted-index data structure to convert content into tokens and index tokens
2. A change data pipeline that captures the last processed time
3. A queue to make sure that file content download and ingestion can be resilient