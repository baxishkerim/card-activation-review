package com.example.cardactivation.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "file:/app/config/ebws.properties", ignoreResourceNotFound = true)
@PropertySource(value = "file:/app/service/card-activation/application.properties", ignoreResourceNotFound = true)
public class WebConfig {
    @Bean
    Logger logger() {
        return LogManager.getLogger("service_logger");
    }

}
