package com.skywalker.envprinter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "env.printer")
public class EnvPrinterProperties {

    private boolean enabled = true;

    private boolean endpointEnabled = true;

    private boolean projectOnly = false;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEndpointEnabled() {
        return endpointEnabled;
    }

    public void setEndpointEnabled(boolean endpointEnabled) {
        this.endpointEnabled = endpointEnabled;
    }

    public boolean isProjectOnly() {
        return projectOnly;
    }

    public void setProjectOnly(boolean projectOnly) {
        this.projectOnly = projectOnly;
    }
}