package com.atviettelsolutions.config.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.annotation.PostConstruct;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDbKpiLogManager {
    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static ApplicationContext applicationContext;
    @PostConstruct
    public void init() {
        Environment env = applicationContext.getEnvironment();
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        String url = env.getProperty("kpi.datasource.url");
        if (url == null) {
            throw new IllegalArgumentException("Missing connection string, please provider it in kpi.datasource.url");
        }
        ConnectionString connectionString = new ConnectionString(url);
        String databaseName = connectionString.getDatabase();
        if (databaseName == null) {
            throw new IllegalArgumentException("Connection string must include database name");
        }
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .codecRegistry(pojoCodecRegistry)
                .build();
        mongoClient = MongoClients.create(settings);
        mongoDatabase = mongoClient.getDatabase(databaseName);
    }
    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        MongoDbKpiLogManager.applicationContext = applicationContext;
    }

    public static <T> MongoCollection<T> getCollection(String collectionName, Class<T> documentClass) {
        return mongoDatabase.getCollection(collectionName, documentClass);
    }
}
