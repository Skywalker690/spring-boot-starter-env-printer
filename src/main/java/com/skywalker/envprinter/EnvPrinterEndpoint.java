package com.skywalker.envprinter;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;

/**
 * Spring Boot Actuator endpoint that exposes environment variables.
 * This endpoint is available at /actuator/envprinter when management endpoints are enabled.
 * 
 * <p>Example usage:</p>
 * <pre>
 * GET /actuator/envprinter
 * </pre>
 * 
 * <p>Returns a JSON object containing filtered environment variables sorted by key.</p>
 */
@Endpoint(id = "envprinter")
public class EnvPrinterEndpoint {

    private final EnvFilterService filterService;

    public EnvPrinterEndpoint(EnvFilterService filterService) {
        this.filterService = filterService;
    }

    /**
     * Returns filtered environment variables as a sorted map.
     * The variables are sorted alphabetically by key for better readability.
     * Filtering is based on the configuration properties.
     * When showValues is false, only variable names are returned (with empty string values).
     *
     * @return a map of environment variable names to their values (or empty strings if showValues is false)
     */
    @ReadOperation
    public Map<String, String> getEnvironment() {
        return filterService.getFilteredEnvironmentForEndpoint();
    }
}
