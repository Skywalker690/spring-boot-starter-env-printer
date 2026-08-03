package com.skywalker.envprinter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class EnvPrinterAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(EnvPrinterAutoConfiguration.class);

    @Test
    void endpointsAreDisabledByDefault() {
        contextRunner.run(context -> assertThat(context).doesNotHaveBean(EnvPrinterController.class)
                .doesNotHaveBean(EnvPrinterEndpoint.class));
    }

    @Test
    void coreBeansRemainAvailableWhenEndpointsAreDisabled() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(EnvPrinter.class)
                .hasSingleBean(EnvFilterService.class)
                .hasSingleBean(EnvUsageScanner.class));
    }

    @Test
    void disablingThePrinterDisablesAllBeans() {
        contextRunner.withPropertyValues("env.printer.enabled=false")
                .run(context -> assertThat(context).doesNotHaveBean(EnvPrinter.class)
                        .doesNotHaveBean(EnvFilterService.class));
    }
}
