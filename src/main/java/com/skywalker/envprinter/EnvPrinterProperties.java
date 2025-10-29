package com.skywalker.envprinter;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Environment Printer starter.
 * Allows customization of the behavior through application.properties or application.yml.
 */
@ConfigurationProperties(prefix = "env.printer")
public class EnvPrinterProperties {

    /**
     * Enable or disable the environment printer functionality.
     * When disabled, no environment variables will be printed at startup
     * and the endpoint will not be available.
     */
    private boolean enabled = true;

    /**
     * Enable or disable the HTTP endpoint for environment variables.
     * When disabled, only startup logging will occur.
     */
    private boolean endpointEnabled = true;

    /**
     * Filter to show only environment variables actually used in the project.
     * When enabled, scans configuration files (application.properties, application.yml)
     * and source code to identify which environment variables are referenced,
     * and displays only those variables.
     */
    private boolean projectOnly = true;

    /**
     * Control whether to display environment variable values or just names.
     * When false (default), only environment variable names are shown in logs and endpoints.
     * When true, both names and values are displayed.
     */
    private boolean showValues = false;

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

    public boolean isShowValues() {
        return showValues;
    }

    public void setShowValues(boolean showValues) {
        this.showValues = showValues;
    }
}