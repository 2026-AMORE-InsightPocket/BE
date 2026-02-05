package com.pocketmon.insightpocket.global.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<IngestApiKeyFilter> ingestApiKeyFilterRegistration(IngestApiKeyFilter filter) {
        FilterRegistrationBean<IngestApiKeyFilter> reg = new FilterRegistrationBean<>();
        reg.setFilter(filter);
        reg.addUrlPatterns("/api/*");
        reg.setOrder(0);
        return reg;
    }
}