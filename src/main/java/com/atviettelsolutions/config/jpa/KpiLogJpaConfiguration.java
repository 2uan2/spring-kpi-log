package com.atviettelsolutions.config.jpa;

import com.atviettelsolutions.repository.KpiLogRepository;
import com.atviettelsolutions.repository.impl.jpa.JpaKpiLogRepositoryImpl;
import com.atviettelsolutions.services.mapper.LogMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class KpiLogJpaConfiguration  {
    @Bean
    public KpiLogRepository kpiLogRepository(LogMapper logMapper) {
        return new JpaKpiLogRepositoryImpl(logMapper);
    }

    @Bean
    public JpaKpiLogDatabaseManager jpaKpiLogDatabaseManager() {
        return new JpaKpiLogDatabaseManager();
    }

    @Bean
    public LogMapper logMapper() {
        return Mappers.getMapper(LogMapper.class);
    }
}
