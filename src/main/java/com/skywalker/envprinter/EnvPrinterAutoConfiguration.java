package com.skywalker.envprinter;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for the Environment Printer starter.
 * This configuration is automatically activated when the starter is on the classpath.
 * It can be disabled by setting {@code env.printer.enabled=false} in application properties.
 */
@AutoConfiguration
@EnableConfigurationProperties(EnvPrinterProperties.class)
@ConditionalOnProperty(prefix = "env.printer", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EnvPrinterAutoConfiguration {

    /**
     * Creates the environment usage scanner that scans project files
     * to identify which environment variables are actually used.
     *
     * @return the EnvUsageScanner instance
     */
    @Bean
    public EnvUsageScanner envUsageScanner() {
        return new EnvUsageScanner();
    }

    /**
     * Creates the filter service for environment variables.
     *
     * @param properties the configuration properties
     * @param scanner the usage scanner
     * @return the EnvFilterService instance
     */
    @Bean
    public EnvFilterService envFilterService(EnvPrinterProperties properties, EnvUsageScanner scanner) {
        return new EnvFilterService(properties, scanner);
    }

    /**
     * Creates the EnvPrinter bean that logs environment variables at startup.
     *
     * @param filterService the filter service
     * @param properties the configuration properties
     * @return the EnvPrinter instance
     */
    @Bean
    public EnvPrinter envPrinter(EnvFilterService filterService, EnvPrinterProperties properties) {
        return new EnvPrinter(filterService, properties);
    }

    /**
     * Creates the REST controller endpoint for environment variables.
     * Only created when endpoint is enabled and application is a web application.
     *
     * @param filterService the filter service
     * @return the EnvPrinterController instance
     */
    @Bean
    @ConditionalOnWebApplication
    @ConditionalOnProperty(prefix = "env.printer", name = "endpoint-enabled", havingValue = "true", matchIfMissing = true)
    public EnvPrinterController envPrinterController(EnvFilterService filterService) {
        return new EnvPrinterController(filterService);
    }

    /**
     * Creates the actuator endpoint for environment variables (optional).
     * Only created when actuator is on the classpath and endpoint is enabled.
     *
     * @param filterService the filter service
     * @return the EnvPrinterEndpoint instance
     */
    @Bean
    @ConditionalOnClass(name = "org.springframework.boot.actuate.endpoint.annotation.Endpoint")
    @ConditionalOnProperty(prefix = "env.printer", name = "endpoint-enabled", havingValue = "true", matchIfMissing = true)
    public EnvPrinterEndpoint envPrinterEndpoint(EnvFilterService filterService) {
        return new EnvPrinterEndpoint(filterService);
    }
}
