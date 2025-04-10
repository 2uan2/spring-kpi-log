package com.atviettelsolutions.repository;

import com.atviettelsolutions.domain.KpiLog;

public interface KpiLogRepository {
    void writeLog(KpiLog kpiLog);
}
