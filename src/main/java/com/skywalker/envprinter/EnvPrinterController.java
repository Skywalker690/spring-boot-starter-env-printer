package com.skywalker.envprinter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST controller that exposes environment variables via HTTP endpoint.
 * This endpoint is available at /env/env-printer and does not require Spring Boot Actuator.
 * 
 * <p>Example usage:</p>
 * <pre>
 * GET /env/env-printer
 * </pre>
 * 
 * <p>Returns a JSON object containing filtered environment variables sorted by key.</p>
 */
@RestController
@RequestMapping("/env")
public class EnvPrinterController {

    private final EnvFilterService filterService;

    public EnvPrinterController(EnvFilterService filterService) {
        this.filterService = filterService;
    }

    /**
     * Returns filtered environment variables as a sorted map.
     * The variables are sorted alphabetically by key for better readability.
     * Filtering is based on the configuration properties.
     *
     * @return a map of environment variable names to their values
     */
    @GetMapping("/env-printer")
    public Map<String, String> getEnvironment() {
        return filterService.getFilteredEnvironment();
    }
}
