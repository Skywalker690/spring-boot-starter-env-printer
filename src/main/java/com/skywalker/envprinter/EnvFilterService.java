package com.skywalker.envprinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class EnvFilterService {

    private static final Logger logger = LoggerFactory.getLogger(EnvFilterService.class);

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

    public Map<String, String> getFilteredEnvironment() {
        Map<String, String> allEnv = System.getenv();
        
        if (properties.isProjectOnly()) {
            return filterProjectOnly(allEnv);
        } else {
            return filterExcluded(allEnv);
        }
    }

    private Map<String, String> filterProjectOnly(Map<String, String> env) {
        // Scan for used variables (cache the result for performance)
        if (cachedUsedVars == null) {
            logger.info("Scanning project for environment variable usage...");
            cachedUsedVars = scanner.scanForUsedEnvVariables();
            
            // Log the detected environment variables with their values
            if (cachedUsedVars.isEmpty()) {
                logger.info("No environment variables detected in project");
            } else {
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
            }
        }
        
        final Set<String> usedVars = cachedUsedVars;
        
        return env.entrySet().stream()
                .filter(entry -> usedVars.contains(entry.getKey()))
                .filter(entry -> !isHardcodedOsExclusion(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        TreeMap::new
                ));
    }

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
