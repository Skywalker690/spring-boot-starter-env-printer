package com.skywalker.envprinter;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class EnvPrinter {

    private static final Logger logger = LoggerFactory.getLogger(EnvPrinter.class);
    private final EnvFilterService filterService;

    public EnvPrinter(EnvFilterService filterService) {
        this.filterService = filterService;
    }

    @PostConstruct
    public void printEnv() {
        logger.info("===============================");
        logger.info("🌍 Environment Variables");
        logger.info("===============================");
        
        Map<String, String> filteredEnv = filterService.getFilteredEnvironment();
        for (Map.Entry<String, String> entry : filteredEnv.entrySet()) {
            logger.info("{} = {}", entry.getKey(), entry.getValue());
        }
        logger.info("===============================");
    }
}
