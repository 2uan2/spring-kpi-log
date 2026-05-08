package com.atviettelsolutions.config.kafka;

import com.atviettelsolutions.repository.KpiLogRepository;
import com.atviettelsolutions.repository.impl.kafka.KafkaKpiLogRepositoryImpl;
import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class KpiLogKafkaConfiguration {
    @Bean
    public KpiLogRepository kpiLogRepository(KafkaKpiLogManager kafkaKpiLogManager) {
        return new KafkaKpiLogRepositoryImpl(kafkaKpiLogManager);
    }

    @Bean
    public KafkaKpiLogManager kafkaKpiLogManager(Environment environment, Gson gson) {
        return new KafkaKpiLogManager(environment, gson);
    }
}
