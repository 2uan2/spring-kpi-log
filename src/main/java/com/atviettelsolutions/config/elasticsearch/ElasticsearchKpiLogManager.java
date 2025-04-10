package com.atviettelsolutions.config.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import jakarta.annotation.PostConstruct;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;

import java.util.UUID;

public class ElasticsearchKpiLogManager {
    private static ElasticsearchClient client;
    private static ElasticsearchOperations operations;
    private static RestClient restClient;
    private static ApplicationContext applicationContext;
    @PostConstruct
    public void init() {
        Environment env = applicationContext.getEnvironment();
        String uri = env.getProperty("kpi.datasource.url");
        if (uri == null) {
            throw new IllegalArgumentException("Missing elasticsearch uri, please provide it in kpi.datasource.url");
        }
        String host = uri.split(":")[0];
        int port = Integer.parseInt(uri.split(":")[1]);
        String username = env.getProperty("kpi.datasource.username", "");
        String password = env.getProperty("kpi.datasource.password", "");
        RestClientBuilder builder = RestClient.builder(new HttpHost(host, port, "http"));
        if (!username.isEmpty() && !password.isEmpty()) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            builder.setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                @Override
                public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpAsyncClientBuilder) {
                    return httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
            });
        }
        restClient = builder.build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
        operations = new ElasticsearchTemplate(client);
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        ElasticsearchKpiLogManager.applicationContext = applicationContext;
    }

    public static <T> void insertDocument(String indexName, T object) {
        IndexQuery indexQuery = new IndexQueryBuilder()
                .withId(UUID.randomUUID().toString())
                .withObject(object)
                .build();
        operations.index(indexQuery, IndexCoordinates.of(indexName));
    }
}
