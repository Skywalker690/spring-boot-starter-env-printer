package com.skywalker.envprinter;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;
import java.util.TreeMap;

@Endpoint(id = "envprinter")
public class EnvPrinterEndpoint {

    @ReadOperation
    public Map<String, String> getEnvironment() {
        // Return sorted environment variables for consistent ordering
        return new TreeMap<>(System.getenv());
    }
}
