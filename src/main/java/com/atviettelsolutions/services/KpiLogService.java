package com.atviettelsolutions.services;

import com.atviettelsolutions.domain.KpiLog;
import com.atviettelsolutions.repository.KpiLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class KpiLogService {
    private final KpiLogRepository kpiLogRepository;

    public void save(KpiLog kpiLog) {
        kpiLogRepository.writeLog(kpiLog);
    }
}
