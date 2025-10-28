package com.skywalker.envprinter;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;

public class EnvPrinter {

    private static final Logger logger = LoggerFactory.getLogger(EnvPrinter.class);

    @PostConstruct
    public void printEnv() {
        logger.info("===============================");
        logger.info("🌍 Environment Variables");
        logger.info("===============================");
        
        // Sort environment variables for better readability
        Map<String, String> sortedEnv = new TreeMap<>(System.getenv());
        for (Map.Entry<String, String> entry : sortedEnv.entrySet()) {
            logger.info("{} = {}", entry.getKey(), entry.getValue());
        }
        logger.info("===============================");
    }
}
