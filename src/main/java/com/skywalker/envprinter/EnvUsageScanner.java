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

/**
 * Service that scans project files to identify environment variables actually used in the application.
 * Scans configuration files (application.properties, application.yml) and Java source files
 * for environment variable references.
 */
public class EnvUsageScanner {

    private static final Logger logger = LoggerFactory.getLogger(EnvUsageScanner.class);

    // Pattern to match ${ENV_VAR} in configuration files
    private static final Pattern PROPERTY_PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{([^}:]+)(?::[^}]*)?\\}");
    
    // Pattern to match System.getenv("ENV_VAR") in Java files
    private static final Pattern GETENV_PATTERN = Pattern.compile("System\\.getenv\\([\"']([^\"']+)[\"']\\)");
    
    // Pattern to match @Value("${ENV_VAR}") annotations
    private static final Pattern VALUE_ANNOTATION_PATTERN = Pattern.compile("@Value\\([\"']\\$\\{([^}:]+)(?::[^}]*)?\\}[\"']\\)");

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

    /**
     * Scans the project to find all environment variables that are actually referenced.
     * 
     * @return set of environment variable names used in the project
     */
    public Set<String> scanForUsedEnvVariables() {
        Set<String> usedVars = new HashSet<>();
        
        try {
            // Scan application.properties files
            scanConfigFiles("classpath*:application*.properties", usedVars);
            
            // Scan application.yml files
            scanConfigFiles("classpath*:application*.yml", usedVars);
            scanConfigFiles("classpath*:application*.yaml", usedVars);
            
            // Scan Java source files (if available in classpath)
            scanJavaFiles(usedVars);
            
            logger.debug("Found {} environment variables in use across the project", usedVars.size());
            
        } catch (Exception e) {
            logger.warn("Error scanning project for environment variable usage: {}", e.getMessage());
        }
        
        return usedVars;
    }

    /**
     * Scans configuration files for environment variable references.
     */
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

    /**
     * Scans Java files for System.getenv() and @Value annotations.
     */
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

    /**
     * Reads a resource and extracts environment variable names using the given pattern.
     */
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
                
                // Also check for @Value annotations and System.getenv
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

    /**
     * Trims prefixes from variable names to get the actual environment variable name.
     * For example, "env.DB_PASSWORD" becomes "DB_PASSWORD"
     */
    private String trimEnvVarName(String varName) {
        if (varName == null || varName.isEmpty()) {
            return varName;
        }
        
        // Remove common prefixes
        String[] prefixes = {"env.", "environment.", "sys.", "system."};
        for (String prefix : prefixes) {
            if (varName.startsWith(prefix)) {
                return varName.substring(prefix.length());
            }
        }
        
        return varName;
    }

    /**
     * Checks if a variable name looks like an environment variable.
     * Environment variables are typically UPPERCASE_WITH_UNDERSCORES.
     */
    private boolean isLikelyEnvVar(String varName) {
        if (varName == null || varName.isEmpty()) {
            return false;
        }
        // Check if it's mostly uppercase or contains underscores (common env var pattern)
        return varName.matches("^[A-Z][A-Z0-9_]*$") || varName.contains("_");
    }
}
