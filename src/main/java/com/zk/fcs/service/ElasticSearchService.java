package com.zk.fcs.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchAllQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.zk.fcs.entity.FileIndex;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.action.index.IndexRequest;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Service
public class ElasticSearchService {

    private ElasticsearchClient elasticsearchClient;
    private final String esHost;
    private final String esPort;
    private final String esMethod;
    private final String username;
    private final String password;

    private final String QueryIndexOnContentAndMeta = "query-index-on-content-and-meta";

    public ElasticSearchService(
            @Value("${spring.elasticsearch.rest.host}") String esHost,
            @Value("${spring.elasticsearch.rest.port}") String esPort,
            @Value("${spring.elasticsearch.rest.method}") String esMethod,
            @Value("${spring.elasticsearch.rest.username}") String username,
            @Value("${spring.elasticsearch.rest.password}") String password
    ) {
        this.esHost = esHost;
        this.esPort = esPort;
        this.esMethod = esMethod;
        this.username = username;
        this.password = password;
        String serverUrl = esMethod + "://" + esHost + ":" + esPort;

        try {
            String encoding = Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
            SSLContextBuilder sslBuilder = SSLContexts.custom()
                    .loadTrustMaterial(null, (x509Certificates, s) -> true);
            final SSLContext sslContext = sslBuilder.build();
            RestClient restClient = RestClient
                    .builder(new HttpHost(esHost, Integer.parseInt(esPort), esMethod))
                    .setDefaultHeaders(new Header[]{
                            new BasicHeader("Authorization", "Basic " + encoding)
                    })
                    .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        @Override
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                            return httpClientBuilder
                                    .setSSLContext(sslContext)
                                    .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE);
                        }
                    })
                    .build();
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());
            this.elasticsearchClient = new ElasticsearchClient(transport);
        } catch (NoSuchAlgorithmException | KeyStoreException | KeyManagementException e) {
        }
    }

    @PostConstruct
    private void init() throws IOException {
        String queryIndexOnContentAndMeta = "{\n" +
                "    \"_source\": [\"fileName\", \"tenant\"],\n" +
                "    \"query\": {\n" +
                "      \"bool\": {\n" +
                "        \"should\": [\n" +
                "           { \"match\": { \"{{fileContent}}\": \"{{fileContentValue}}\" }},\n" +
                "           { \"match\": { \"{{metaData}}\": \"{{metaDataValue}}\" }}\n" +
                "         ]\n" +
                "       }\n" +
                "      }\n" +
                "}";
        elasticsearchClient.putScript(r -> r
                .id(QueryIndexOnContentAndMeta)
                .script(s -> s
                        .lang("mustache")
                        .source(queryIndexOnContentAndMeta)
                ));
    }


    public void indexFileIndexDocument(String index, FileIndex document) throws IOException {
        IndexRequest request = new IndexRequest(index);
        request.source(document, XContentType.JSON);
        elasticsearchClient.index(i -> i
                .index(index)
                .document(document));
    }

    public <T> List<T> searchOnContentAndIndex(String index, String term, Class<T> tClass) throws IOException {
        SearchTemplateResponse<T> response = elasticsearchClient.searchTemplate(r -> r
                        .index(index)
                        .id(QueryIndexOnContentAndMeta)
                        .params("fileContent", JsonData.of("fileContent"))
                        .params("fileContentValue", JsonData.of(term))
                        .params("metaData", JsonData.of("metaData"))
                        .params("metaDataValue", JsonData.of(term)),
                tClass
        );

        List<Hit<T>> hits = response.hits().hits();
        List<T> tList = new ArrayList<>();
        for (Hit<T> hit: hits) {
            tList.add(hit.source());

        }
        return tList;
    }

    public void deleteAll(String index) throws IOException {
        DeleteByQueryRequest request = DeleteByQueryRequest.of(d -> d.index(index)
                .query(new MatchAllQuery.Builder().build()._toQuery()).refresh(true));
        elasticsearchClient.deleteByQuery(request);
    }
}
