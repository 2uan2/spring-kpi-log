package com.atviettelsolutions.config.elasticsearch;

import com.atviettelsolutions.repository.KpiLogRepository;
import com.atviettelsolutions.repository.impl.elasticsearch.ElasticsearchKpiLogRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KpiLogElasticsearchConfiguration {
    @Bean
    public ElasticsearchKpiLogManager elasticsearchKpiLogManager() {
        return new ElasticsearchKpiLogManager();
    }
    @Bean
    public KpiLogRepository kpiLogRepository() {
        return new ElasticsearchKpiLogRepositoryImpl();
    }
}
