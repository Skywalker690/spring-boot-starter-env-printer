package com.skywalker.envprinter;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EnvUsageScannerTest {

    @Test
    void findsEnvironmentPlaceholdersInClasspathConfiguration() {
        Set<String> variables = new EnvUsageScanner().scanForUsedEnvVariables();

        assertTrue(variables.contains("TEST_DATABASE_URL"));
        assertTrue(variables.contains("TEST_API_KEY"));
    }

    @Test
    void scansOnlyConfiguredPackagesWhenCompiledScanningIsEnabled() {
        EnvPrinterProperties properties = new EnvPrinterProperties();
        properties.setScanCompiledClasses(true);
        properties.setScanPackages(java.util.List.of("com.skywalker.envprinter"));

        Set<String> variables = new EnvUsageScanner(properties).scanForUsedEnvVariables();

        assertTrue(variables.contains("COMPILED_ENV_KEY"));
    }

    static String compiledEnvironmentReference() {
        return System.getenv("COMPILED_ENV_KEY");
    }
}
