package com.skywalker.envprinter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for the Environment Printer starter.
 * Allows customization of the behavior through application.properties or application.yml.
 */
@ConfigurationProperties(prefix = "env.printer")
public class EnvPrinterProperties {

    /**
     * Creates properties with safe defaults.
     */
    public EnvPrinterProperties() {
    }

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
    private boolean endpointEnabled = false;

    /**
     * Filter to show only environment variables actually used in the project.
     * When enabled, scans configuration files and optionally configured compiled
     * application packages to identify which environment variables are referenced,
     * and displays only those variables.
     */
    private boolean projectOnly = true;

    /**
     * Control whether to display environment variable values or just names.
     * When false (default), only environment variable names are shown in logs and endpoints.
     * When true, both names and values are displayed.
     */
    private boolean showValues = false;

    /**
     * Enable scanning compiled classes for environment-variable references.
     * Disabled by default because package-scoped scanning must be configured.
     */
    private boolean scanCompiledClasses = false;

    /**
     * Application package prefixes to scan when compiled-class scanning is enabled.
     */
    private List<String> scanPackages = new ArrayList<>();

    /**
     * Returns whether the printer is enabled.
     *
     * @return whether the printer is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether the printer is enabled.
     *
     * @param enabled whether the printer is enabled
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether HTTP endpoints are enabled.
     *
     * @return whether HTTP endpoints are enabled
     */
    public boolean isEndpointEnabled() {
        return endpointEnabled;
    }

    /**
     * Sets whether HTTP endpoints are enabled.
     *
     * @param endpointEnabled whether HTTP endpoints are enabled
     */
    public void setEndpointEnabled(boolean endpointEnabled) {
        this.endpointEnabled = endpointEnabled;
    }

    /**
     * Returns whether project-only filtering is enabled.
     *
     * @return whether project-only filtering is enabled
     */
    public boolean isProjectOnly() {
        return projectOnly;
    }

    /**
     * Sets whether project-only filtering is enabled.
     *
     * @param projectOnly whether project-only filtering is enabled
     */
    public void setProjectOnly(boolean projectOnly) {
        this.projectOnly = projectOnly;
    }

    /**
     * Returns whether environment values are shown.
     *
     * @return whether environment values are shown
     */
    public boolean isShowValues() {
        return showValues;
    }

    /**
     * Sets whether environment values are shown.
     *
     * @param showValues whether environment values are shown
     */
    public void setShowValues(boolean showValues) {
        this.showValues = showValues;
    }

    /**
     * Returns whether compiled-class scanning is enabled.
     *
     * @return true when compiled classes should be scanned
     */
    public boolean isScanCompiledClasses() {
        return scanCompiledClasses;
    }

    /**
     * Enables or disables compiled-class scanning.
     *
     * @param scanCompiledClasses whether compiled classes should be scanned
     */
    public void setScanCompiledClasses(boolean scanCompiledClasses) {
        this.scanCompiledClasses = scanCompiledClasses;
    }

    /**
     * Returns the configured application package prefixes.
     *
     * @return package prefixes to scan
     */
    public List<String> getScanPackages() {
        return scanPackages;
    }

    /**
     * Sets application package prefixes for compiled-class scanning.
     *
     * @param scanPackages package prefixes to scan
     */
    public void setScanPackages(List<String> scanPackages) {
        this.scanPackages = scanPackages == null ? new ArrayList<>() : new ArrayList<>(scanPackages);
    }
}
