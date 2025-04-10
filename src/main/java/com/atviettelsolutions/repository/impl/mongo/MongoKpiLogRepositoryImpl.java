package com.atviettelsolutions.repository.impl.mongo;

import com.atviettelsolutions.config.mongo.MongoDbKpiLogManager;
import com.atviettelsolutions.domain.KpiLog;
import com.atviettelsolutions.repository.KpiLogRepository;


public class MongoKpiLogRepositoryImpl implements KpiLogRepository {

    @Override
    public void writeLog(KpiLog kpiLog) {
        MongoDbKpiLogManager.getCollection("kpi_log", KpiLog.class).insertOne(kpiLog);
    }
}
