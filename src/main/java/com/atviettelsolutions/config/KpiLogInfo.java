package com.atviettelsolutions.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "kpi")
@Data
public class KpiLogInfo {
    private List<IgnoreRoute> ignoreRestRoutes;
    private List<String> ignoreGrpcMethods;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IgnoreRoute {
        private String path;
        private String method;
    }
}
