package com.atviettelsolutions.repository.impl.elasticsearch;

import com.atviettelsolutions.config.elasticsearch.ElasticsearchKpiLogManager;
import com.atviettelsolutions.domain.KpiLog;
import com.atviettelsolutions.repository.KpiLogRepository;

public class ElasticsearchKpiLogRepositoryImpl implements KpiLogRepository {

    @Override
    public void writeLog(KpiLog kpiLog) {
        ElasticsearchKpiLogManager.insertDocument("kpi_log", kpiLog);
    }
}
