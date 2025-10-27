package com.skywalker.envprinter;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Endpoint(id = "env-printer")
public class EnvPrinterEndpoint {

    @ReadOperation
    public Map<String, String> getEnvironment() {
        return System.getenv();
    }
}
