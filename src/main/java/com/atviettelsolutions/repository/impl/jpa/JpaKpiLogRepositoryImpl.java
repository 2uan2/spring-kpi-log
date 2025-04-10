package com.atviettelsolutions.repository.impl.jpa;

import com.atviettelsolutions.config.jpa.JpaKpiLogDatabaseManager;
import com.atviettelsolutions.domain.KpiLog;
import com.atviettelsolutions.domain.Log;
import com.atviettelsolutions.repository.KpiLogRepository;
import com.atviettelsolutions.services.mapper.LogMapper;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@AllArgsConstructor
public class JpaKpiLogRepositoryImpl implements KpiLogRepository {
    private final LogMapper logMapper;
    @Transactional
    @Override
    public void writeLog(KpiLog kpiLog) {
        Log log = logMapper.toEntity(kpiLog);
        log.setId(UUID.randomUUID().toString());
        JpaKpiLogDatabaseManager.save(log);
    }
}
