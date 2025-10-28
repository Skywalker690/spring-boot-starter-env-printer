package com.skywalker.envprinter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;


@AutoConfiguration
@EnableConfigurationProperties(EnvPrinterProperties.class)
@ConditionalOnProperty(prefix = "env.printer", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EnvPrinterAutoConfiguration {

    @Bean
    public EnvPrinter envPrinter() {
        return new EnvPrinter();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.endpoint.annotation.Endpoint")
    @ConditionalOnProperty(prefix = "env.printer", name = "endpoint-enabled", havingValue = "true", matchIfMissing = true)
    public EnvPrinterEndpoint envPrinterEndpoint() {
        return new EnvPrinterEndpoint();
    }
}
