package com.skywalker.envprinter;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnProperty(prefix = "env.printer", name = "enabled", havingValue = "true", matchIfMissing = true)
public class EnvPrinterAutoConfiguration {

    @Bean
    public EnvPrinter printer() {
        return new EnvPrinter();
    }
}
