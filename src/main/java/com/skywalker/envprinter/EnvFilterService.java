package com.skywalker.envprinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Service for filtering and retrieving environment variables based on configuration.
 * When project-only mode is enabled, scans the entire project to identify which
 * environment variables are actually used.
 */
public class EnvFilterService {

    private static final Logger logger = LoggerFactory.getLogger(EnvFilterService.class);

    /**
     * Hardcoded list of common OS-specific environment variables to always exclude.
     * These are system/OS variables that are rarely relevant to application debugging.
     */
    private static final Set<String> HARDCODED_OS_EXCLUSIONS = new HashSet<>(Arrays.asList(
            // Windows-specific
            "ALLUSERSPROFILE", "APPDATA", "CommonProgramFiles", "CommonProgramFiles(x86)",
            "CommonProgramW6432", "COMPUTERNAME", "ComSpec", "DriverData",
            "HOMEDRIVE", "HOMEPATH", "LOCALAPPDATA", "LOGONSERVER",
            "OneDrive", "OneDriveCommercial", "OneDriveConsumer",
            "ProgramData", "ProgramFiles", "ProgramFiles(x86)", "ProgramW6432",
            "PUBLIC", "SystemDrive", "SystemRoot", "TEMP", "TMP",
            "USERDOMAIN", "USERDOMAIN_ROAMINGPROFILE", "USERNAME", "USERPROFILE",
            "windir", "OS", "PATHEXT", "PROMPT", "PSModulePath",
            
            // Processor-related (Windows)
            "PROCESSOR_ARCHITECTURE", "PROCESSOR_IDENTIFIER", "PROCESSOR_LEVEL",
            "PROCESSOR_REVISION", "NUMBER_OF_PROCESSORS",
            
            // Session/display (Linux/Unix)
            "DISPLAY", "SESSION_MANAGER", "XDG_SESSION_ID", "XDG_SESSION_TYPE",
            "XDG_SESSION_CLASS", "XDG_RUNTIME_DIR", "XDG_CONFIG_HOME",
            "XDG_CURRENT_DESKTOP", "XDG_SESSION_DESKTOP",
            
            // Shell/terminal
            "SHLVL", "SHELL", "TERM", "COLORTERM", "TERMINAL_EMULATOR",
            
            // Locale/language (can be noisy)
            "LANG", "LANGUAGE", "LC_ALL", "LC_CTYPE"
    ));

    private final EnvPrinterProperties properties;
    private final EnvUsageScanner scanner;
    private Set<String> cachedUsedVars = null;

    public EnvFilterService(EnvPrinterProperties properties, EnvUsageScanner scanner) {
        this.properties = properties;
        this.scanner = scanner;
    }

    /**
     * Gets filtered environment variables based on configuration.
     * 
     * @return filtered and sorted map of environment variables
     */
    public Map<String, String> getFilteredEnvironment() {
        Map<String, String> allEnv = System.getenv();
        
        if (properties.isProjectOnly()) {
            return filterProjectOnly(allEnv);
        } else {
            return filterExcluded(allEnv);
        }
    }

    /**
     * Gets filtered environment variables for API/endpoint response.
     * Returns only names if showValues is false, otherwise returns name-value pairs.
     * 
     * @return filtered and sorted map of environment variables (value may be empty string if showValues is false)
     */
    public Map<String, String> getFilteredEnvironmentForEndpoint() {
        Map<String, String> filteredEnv = getFilteredEnvironment();
        
        if (!properties.isShowValues()) {
            // Return map with empty string values (just showing keys)
            return filteredEnv.keySet().stream()
                    .collect(Collectors.toMap(
                            key -> key,
                            key -> "",
                            (e1, e2) -> e1,
                            TreeMap::new
                    ));
        }
        
        return filteredEnv;
    }

    /**
     * Filters environment variables to show only those actually used in the project.
     * Scans configuration files and source code to identify referenced variables.
     */
    private Map<String, String> filterProjectOnly(Map<String, String> env) {
        // Scan for used variables (cache the result for performance)
        if (cachedUsedVars == null) {
            logger.info("Scanning project for environment variable usage...");
            cachedUsedVars = scanner.scanForUsedEnvVariables();
            
            // Log the detected environment variables
            if (cachedUsedVars.isEmpty()) {
                logger.info("No environment variables detected in project");
            } else {
                if (properties.isShowValues()) {
                    // Build a formatted string with name=value pairs
                    String detectedVarsWithValues = cachedUsedVars.stream()
                            .sorted()
                            .map(varName -> {
                                String value = env.get(varName);
                                if (value != null) {
                                    return varName + "=" + value;
                                } else {
                                    return varName + "=<not set>";
                                }
                            })
                            .collect(Collectors.joining(", "));
                    logger.info("Detected environment variables: {}", detectedVarsWithValues);
                } else {
                    // Show only names
                    String detectedVarNames = cachedUsedVars.stream()
                            .sorted()
                            .collect(Collectors.joining(", "));
                    logger.info("Detected environment variables: {}", detectedVarNames);
                }
            }
        }
        
        final Set<String> usedVars = cachedUsedVars;
        
        // Build result map including both set and unset variables
        Map<String, String> result = new TreeMap<>();
        
        // Add all detected variables (set or not set)
        for (String varName : usedVars) {
            if (!isHardcodedOsExclusion(varName)) {
                // Get value from environment, or use null if not set
                result.put(varName, env.get(varName));
            }
        }
        
        return result;
    }

    /**
     * Filters out hardcoded OS-specific environment variables.
     */
    private Map<String, String> filterExcluded(Map<String, String> env) {
        return env.entrySet().stream()
                .filter(entry -> !isHardcodedOsExclusion(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        TreeMap::new
                ));
    }

    /**
     * Checks if an environment variable is in the hardcoded OS exclusion list.
     */
    private boolean isHardcodedOsExclusion(String key) {
        // Exact match
        if (HARDCODED_OS_EXCLUSIONS.contains(key)) {
            return true;
        }
        
        // Prefix match for patterns like PROCESSOR_*, etc.
        for (String excluded : HARDCODED_OS_EXCLUSIONS) {
            if (excluded.endsWith("_") && key.startsWith(excluded)) {
                return true;
            }
        }
        
        return false;
    }
}
