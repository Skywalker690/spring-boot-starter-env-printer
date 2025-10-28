package com.skywalker.envprinter;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;

@Endpoint(id = "envprinter")
public class EnvPrinterEndpoint {

    private final EnvFilterService filterService;

    public EnvPrinterEndpoint(EnvFilterService filterService) {
        this.filterService = filterService;
    }

    @ReadOperation
    public Map<String, String> getEnvironment() {
        return filterService.getFilteredEnvironment();
    }
}
