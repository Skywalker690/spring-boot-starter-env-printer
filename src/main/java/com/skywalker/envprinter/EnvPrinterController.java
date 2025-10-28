package com.skywalker.envprinter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/env")
public class EnvPrinterController {

    private final EnvFilterService filterService;

    public EnvPrinterController(EnvFilterService filterService) {
        this.filterService = filterService;
    }

    @GetMapping("/env-printer")
    public Map<String, String> getEnvironment() {
        return filterService.getFilteredEnvironment();
    }
}
