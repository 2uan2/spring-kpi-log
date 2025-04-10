package com.atviettelsolutions.config;

import com.atviettelsolutions.services.KpiLogService;
import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

public class KpiLogRestConfiguration {

    @Bean
    public ServletRegistrationBean<DispatcherServlet> dispatcherRegistration(KpiLogService kpiLogService, ApplicationInfo applicationInfo, Gson gson, KpiLogInfo kpiLogInfo) {
        return new ServletRegistrationBean(dispatcherServlet(kpiLogService, applicationInfo, gson, kpiLogInfo));
    }

    @Bean(name = DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_BEAN_NAME)
    public DispatcherServlet dispatcherServlet(KpiLogService kpiLogService, ApplicationInfo applicationInfo, Gson gson, KpiLogInfo kpiLogInfo) {
        return new LoggingDispatcherServlet(kpiLogService, applicationInfo, gson, kpiLogInfo);
    }
}
