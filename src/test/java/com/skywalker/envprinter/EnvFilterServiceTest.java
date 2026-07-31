package com.skywalker.envprinter;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnvFilterServiceTest {

    @Test
    void endpointHidesValuesByDefault() {
        EnvPrinterProperties properties = new EnvPrinterProperties();
        EnvFilterService service = new EnvFilterService(properties, new EnvUsageScanner());

        Map<String, String> result = service.getFilteredEnvironmentForEndpoint();

        assertTrue(result.values().stream().allMatch(String::isEmpty));
    }

    @Test
    void endpointIsDisabledByDefault() {
        assertFalse(new EnvPrinterProperties().isEndpointEnabled());
    }
}
