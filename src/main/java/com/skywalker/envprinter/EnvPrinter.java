package com.skywalker.envprinter;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Component that prints environment variables at application startup.
 * Uses SLF4J for proper logging integration with Spring Boot.
 */
public class EnvPrinter {

    private static final Logger logger = LoggerFactory.getLogger(EnvPrinter.class);
    private final EnvFilterService filterService;
    private final EnvPrinterProperties properties;

    /**
     * Creates the startup environment printer.
     *
     * @param filterService environment filtering service
     * @param properties printer configuration
     */
    public EnvPrinter(EnvFilterService filterService, EnvPrinterProperties properties) {
        this.filterService = filterService;
        this.properties = properties;
    }

    /**
     * Logs the configured environment view after application initialization.
     */
    @PostConstruct
    public void printEnv() {
        logger.info("===============================");
        logger.info("🌍 Environment Variables");
        logger.info("===============================");
        
        Map<String, String> filteredEnv = filterService.getFilteredEnvironment();
        
        if (properties.isShowValues()) {
            // Show both name and value
            for (Map.Entry<String, String> entry : filteredEnv.entrySet()) {
                logger.info("{} = {}", entry.getKey(), entry.getValue());
            }
        } else {
            // Show only names
            for (String key : filteredEnv.keySet()) {
                logger.info("{}", key);
            }
        }
        logger.info("===============================");
        logger.info("===============================");


    }
}
