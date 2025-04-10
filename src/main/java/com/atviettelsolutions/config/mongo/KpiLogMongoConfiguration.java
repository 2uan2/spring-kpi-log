package com.atviettelsolutions.config.mongo;

import com.atviettelsolutions.repository.KpiLogRepository;
import com.atviettelsolutions.repository.impl.mongo.MongoKpiLogRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KpiLogMongoConfiguration {
    @Bean
    public MongoDbKpiLogManager mongoDbKpiLogManager() {
        return new MongoDbKpiLogManager();
    }

    @Bean
    public KpiLogRepository kpiLogRepository() {
        return new MongoKpiLogRepositoryImpl();
    }
}
