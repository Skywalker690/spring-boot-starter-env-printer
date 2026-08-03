package com.skywalker.envprinter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service that scans project files to identify environment variables actually used in the application.
 * Scans configuration files and, when explicitly configured, application compiled classes
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
    private final EnvPrinterProperties properties;

    /**
     * Creates a scanner using default properties.
     */
    public EnvUsageScanner() {
        this(new EnvPrinterProperties());
    }

    /**
     * Creates a scanner with the supplied configuration.
     *
     * @param properties scanner configuration
     */
    public EnvUsageScanner(EnvPrinterProperties properties) {
        this.properties = properties;
    }

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
            
            // Scan compiled application classes. Source files are normally not available
            // in a deployed application, so inspect class-file string constants instead.
            if (properties.isScanCompiledClasses()) {
                scanClassFiles(usedVars, properties.getScanPackages());
            }
            
            logger.debug("Found {} environment variables in use across the project", usedVars.size());
            
        } catch (Exception e) {
            logger.warn("Error scanning project for environment variable usage: {}", e.getMessage());
        }
        
        return Collections.unmodifiableSet(new HashSet<>(usedVars));
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
     * Scans compiled classes for System.getenv() calls and @Value annotations.
     */
    private void scanClassFiles(Set<String> usedVars, List<String> scanPackages) {
        if (scanPackages == null || scanPackages.isEmpty()) {
            logger.debug("Compiled-class scanning is enabled but no scan packages are configured");
            return;
        }
        try {
            for (String scanPackage : scanPackages) {
                String normalizedPackage = scanPackage == null ? "" : scanPackage.trim();
                if (normalizedPackage.isEmpty()) {
                    continue;
                }
                String locationPattern = "classpath*:" + normalizedPackage.replace('.', '/') + "/**/*.class";
                Resource[] resources = resolver.getResources(locationPattern);
                for (Resource resource : resources) {
                    if (resource.exists() && resource.isReadable()) {
                        byte[] classBytes;
                        try (var inputStream = resource.getInputStream()) {
                            classBytes = inputStream.readAllBytes();
                        }
                        scanClass(classBytes, usedVars);
                    }
                }
            }
        } catch (IOException e) {
            logger.debug("Could not scan compiled classes: {}", e.getMessage());
        }
    }

    private void scanClass(byte[] classBytes, Set<String> usedVars) {
        new ClassReader(classBytes).accept(new ClassVisitor(Opcodes.ASM9) {
            @Override
            public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                if ("Lorg/springframework/beans/factory/annotation/Value;".equals(descriptor)) {
                    return new AnnotationVisitor(Opcodes.ASM9) {
                        @Override
                        public void visit(String name, Object value) {
                            if (value instanceof String stringValue) {
                                addIfLikelyEnvVar(stringValue, usedVars);
                            }
                        }
                    };
                }
                return null;
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                              String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9) {
                    private String lastStringConstant;

                    @Override
                    public void visitLdcInsn(Object value) {
                        lastStringConstant = value instanceof String ? (String) value : null;
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String methodName,
                                                String methodDescriptor, boolean isInterface) {
                        if ("java/lang/System".equals(owner) && "getenv".equals(methodName)
                                && lastStringConstant != null) {
                            addIfLikelyEnvVar(lastStringConstant, usedVars);
                        }
                        lastStringConstant = null;
                    }
                };
            }
        }, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
    }

    private void addIfLikelyEnvVar(String value, Set<String> usedVars) {
        String varName = trimEnvVarName(value.trim());
        if (isLikelyEnvVar(varName)) {
            usedVars.add(varName);
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
