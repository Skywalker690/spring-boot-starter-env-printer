package com.skywalker.envprinter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@EnableConfigurationProperties(EnvPrinterProperties.class)
@ConditionalOnProperty(prefix = "env.printer", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EnvPrinterAutoConfiguration {

    @Bean
    public EnvFilterService envFilterService(EnvPrinterProperties properties) {
        return new EnvFilterService(properties);
    }

    @Bean
    public EnvPrinter envPrinter(EnvFilterService filterService) {
        return new EnvPrinter(filterService);
    }

    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "env.printer", name = "endpoint-enabled", havingValue = "true", matchIfMissing = true)
    public EnvPrinterController envPrinterController(EnvFilterService filterService) {
        return new EnvPrinterController(filterService);
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.endpoint.annotation.Endpoint")
    @ConditionalOnProperty(prefix = "env.printer", name = "endpoint-enabled", havingValue = "true", matchIfMissing = true)
    public EnvPrinterEndpoint envPrinterEndpoint(EnvFilterService filterService) {
        return new EnvPrinterEndpoint(filterService);
    }
}
