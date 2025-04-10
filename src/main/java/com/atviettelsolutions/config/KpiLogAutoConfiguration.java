package com.atviettelsolutions.config;

import com.atviettelsolutions.repository.KpiLogRepository;
import com.atviettelsolutions.services.KpiLogService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({ApplicationInfo.class, KpiLogInfo.class})
@Slf4j
public class KpiLogAutoConfiguration {
    @Bean
    public KpiLogService kpiLogService(KpiLogRepository kpiLogRepository) {
        return new KpiLogService(kpiLogRepository);
    }

    @Bean
    public Gson gson() {
        return new Gson();
    }
}
