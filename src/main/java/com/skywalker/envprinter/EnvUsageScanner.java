package com.skywalker.envprinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EnvUsageScanner {

    private static final Logger logger = LoggerFactory.getLogger(EnvUsageScanner.class);

    private static final Pattern PROPERTY_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}:]+)(?::[^}]*)?\\}");
    
    private static final Pattern GETENV_PATTERN = Pattern.compile("System\\.getenv\\([\"']([^\"']+)[\"']\\)");
    
    private static final Pattern VALUE_ANNOTATION_PATTERN = Pattern.compile("@Value\\([\"']\\$\\{([^}:]+)(?::[^}]*)?\\}[\"']\\)");

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();


    public Set<String> scanForUsedEnvVariables() {
        Set<String> usedVars = new HashSet<>();
        
        try {
            // Scan application.properties files
            scanConfigFiles("classpath*:application*.properties", usedVars);
            scanConfigFiles("classpath*:application*.yml", usedVars);
            scanConfigFiles("classpath*:application*.yaml", usedVars);
            scanJavaFiles(usedVars);
            
            logger.debug("Found {} environment variables in use across the project", usedVars.size());
            
        } catch (Exception e) {
            logger.warn("Error scanning project for environment variable usage: {}", e.getMessage());
        }
        
        return usedVars;
    }

    private void scanConfigFiles(String locationPattern, Set<String> usedVars) {
        try {
            Resource[] resources = resolver.getResources(locationPattern);
            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable()) {
                    scanResource(resource, PROPERTY_PLACEHOLDER_PATTERN, usedVars);
                }
            }
        } catch (IOException e) {
            logger.debug("Could not scan pattern {}: {}", locationPattern, e.getMessage());
        }
    }

    private void scanJavaFiles(Set<String> usedVars) {
        try {
            Resource[] resources = resolver.getResources("classpath*:**/*.class");
            // Note: Scanning compiled classes is limited. This is a best-effort approach.
            // In a real deployment, source files might not be available.
            logger.debug("Java class scanning is limited in compiled environment");
        } catch (IOException e) {
            logger.debug("Could not scan Java files: {}", e.getMessage());
        }
    }

    private void scanResource(Resource resource, Pattern pattern, Set<String> usedVars) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    String varName = matcher.group(1).trim();
                    // Trim any prefixes like "env." to get the actual env var name
                    varName = trimEnvVarName(varName);
                    // Only add if it looks like an environment variable (uppercase with underscores)
                    if (isLikelyEnvVar(varName)) {
                        usedVars.add(varName);
                        logger.trace("Found environment variable reference: {}", varName);
                    }
                }
                
                Matcher valueAnnotationMatcher = VALUE_ANNOTATION_PATTERN.matcher(line);
                while (valueAnnotationMatcher.find()) {
                    String varName = valueAnnotationMatcher.group(1).trim();
                    varName = trimEnvVarName(varName);
                    if (isLikelyEnvVar(varName)) {
                        usedVars.add(varName);
                        logger.trace("Found @Value environment variable: {}", varName);
                    }
                }
                
                Matcher getenvMatcher = GETENV_PATTERN.matcher(line);
                while (getenvMatcher.find()) {
                    String varName = getenvMatcher.group(1).trim();
                    varName = trimEnvVarName(varName);
                    usedVars.add(varName);
                    logger.trace("Found System.getenv reference: {}", varName);
                }
            }
        } catch (IOException e) {
            logger.debug("Error reading resource {}: {}", resource.getFilename(), e.getMessage());
        }
    }

    private String trimEnvVarName(String varName) {
        if (varName == null || varName.isEmpty()) {
            return varName;
        }
        
        String[] prefixes = {"env.", "environment.", "sys.", "system."};
        for (String prefix : prefixes) {
            if (varName.startsWith(prefix)) {
                return varName.substring(prefix.length());
            }
        }
        
        return varName;
    }

    private boolean isLikelyEnvVar(String varName) {
        if (varName == null || varName.isEmpty()) {
            return false;
        }
        // Check if it's mostly uppercase or contains underscores (common env var pattern)
        return varName.matches("^[A-Z][A-Z0-9_]*$") || varName.contains("_");
    }
}
