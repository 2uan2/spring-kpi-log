package com.atviettelsolutions.config;

import com.atviettelsolutions.services.KpiLogService;
import com.google.gson.Gson;
import io.grpc.ServerInterceptor;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.oauth2.jwt.JwtDecoder;

public class KpiLogGrpcConfiguration {
    @Bean
    @GrpcGlobalServerInterceptor
    @Order(1)
    public ServerInterceptor loggingInterceptor(ApplicationInfo applicationInfo, KpiLogService kpiLogService, GrpcContext grpcContext, Gson gson, KpiLogInfo kpiLogInfo) {
        return new LoggingGrpcInterceptor(applicationInfo, kpiLogService, grpcContext, gson, kpiLogInfo);
    }

    @Bean
    @GrpcGlobalServerInterceptor
    @Order(2)
    public ServerInterceptor authInterceptor(JwtDecoder jwtDecoder, GrpcContext grpcContext) {
        return new JwtGrpcInterceptor(jwtDecoder, grpcContext);
    }

    @Bean
    public GrpcContext grpcContext() {
        return new GrpcContext();
    }
}
