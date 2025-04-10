package com.atviettelsolutions.config;

import com.atviettelsolutions.config.elasticsearch.KpiLogElasticsearchConfiguration;
import com.atviettelsolutions.config.jpa.KpiLogJpaConfiguration;
import com.atviettelsolutions.config.mongo.KpiLogMongoConfiguration;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

public class KpiLogConfigurationSelector implements ImportSelector, EnvironmentAware {
    private Environment environment;
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<String> imports = new ArrayList<>();
        imports.add(KpiLogAutoConfiguration.class.getName());
        imports.add(KpiLogRestConfiguration.class.getName());
        boolean grpcEnabled = environment.getProperty("kpi.grpc.enable", Boolean.class, false);
        if (grpcEnabled) {
            imports.add(KpiLogGrpcConfiguration.class.getName());
        }
        String databaseType = environment.getProperty("kpi.database");
        switch (databaseType) {
            case "mongodb":
                imports.add(KpiLogMongoConfiguration.class.getName());
                break;
            case "postgresql", "mysql", "mariadb":
                imports.add(KpiLogJpaConfiguration.class.getName());
                break;
            case "elasticsearch":
                imports.add(KpiLogElasticsearchConfiguration.class.getName());
                break;
            case null, default:
                throw new IllegalStateException("Unexpected value: " + databaseType);
        }
        return imports.toArray(new String[0]);
    }
}
