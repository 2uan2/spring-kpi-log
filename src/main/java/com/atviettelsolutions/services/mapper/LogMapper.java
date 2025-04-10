package com.atviettelsolutions.services.mapper;

import com.atviettelsolutions.domain.KpiLog;
import com.atviettelsolutions.domain.Log;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogMapper {
    Log toEntity(KpiLog kpiLog);
}
