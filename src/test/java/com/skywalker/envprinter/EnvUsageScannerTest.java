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
}
