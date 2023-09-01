## Remote Storage Files - Meta/Content Search

### External Dependencies
1. Postgres
2. ElasticSearch
3. Kafka

### External dependency documentation
1. Postgres - Is to store the file url and last processed time (CDC)
2. ElasticSearch: 
   - For maintaining inverted indices of file content tokens (single term)
   - How does it work? 
     - Inverted index datastructure - [inverted-index](https://nlp.stanford.edu/IR-book/html/htmledition/a-first-take-at-building-an-inverted-index-1.html) 
     - Lucene the underlying component of ES uses a term dictionary to map a term to the document iterator
     - [Lucene index file format](http://lucene.apache.org/core/8_2_0/core/org/apache/lucene/codecs/lucene80/package-summary.html#package.description)
3. Kafka:
    - When file processing of a s3 or any source is triggered, all files from the source is read and is produced.
    - The file url is the key (so that it is consumed by a specific partition) [Maintain Strong Ordering Guarantees
      ](https://www.confluent.io/blog/apache-kafka-for-service-architectures/)

### Swagger doc
http://localhost:8085/fs/swagger-ui/index.html