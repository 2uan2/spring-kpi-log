package com.atviettelsolutions.repository.impl.kafka;

import com.atviettelsolutions.config.kafka.KafkaKpiLogManager;
import com.atviettelsolutions.domain.KpiLog;
import com.atviettelsolutions.repository.KpiLogRepository;
import com.atviettelsolutions.services.mapper.LogMapper;
import org.springframework.beans.factory.annotation.Autowired;

public class KafkaKpiLogRepositoryImpl implements KpiLogRepository {
    private final KafkaKpiLogManager kafkaKpiLogManager;
    
    public KafkaKpiLogRepositoryImpl(KafkaKpiLogManager kafkaKpiLogManager) {
        this.kafkaKpiLogManager = kafkaKpiLogManager;
    }
    @Override
    public void writeLog(KpiLog kpiLog) {
        kafkaKpiLogManager.send(null, kpiLog);
    }
}
