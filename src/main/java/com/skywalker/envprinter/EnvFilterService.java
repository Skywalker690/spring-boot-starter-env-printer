package com.skywalker.envprinter;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


public class EnvFilterService {

    private final EnvPrinterProperties properties;

    public EnvFilterService(EnvPrinterProperties properties) {
        this.properties = properties;
    }

    public Map<String, String> getFilteredEnvironment() {
        Map<String, String> allEnv = System.getenv();
        
        if (properties.isProjectOnly()) {
            return filterProjectOnly(allEnv);
        } else {
            return filterExcluded(allEnv);
        }
    }

    private Map<String, String> filterProjectOnly(Map<String, String> env) {
        return env.entrySet().stream()
                .filter(entry -> isProjectRelated(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        TreeMap::new
                ));
    }

    private Map<String, String> filterExcluded(Map<String, String> env) {
        return env.entrySet().stream()
                .filter(entry -> !isExcluded(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        TreeMap::new
                ));
    }

    private boolean isProjectRelated(String key) {
        for (String pattern : properties.getIncludePatterns()) {
            if (key.startsWith(pattern)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExcluded(String key) {
        for (String prefix : properties.getExcludePrefixes()) {
            if (key.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
