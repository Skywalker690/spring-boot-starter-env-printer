package com.skywalker.envprinter;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "env.printer")
public class EnvPrinterProperties {

    private boolean enabled = true;

    private boolean endpointEnabled = true;

    private boolean projectOnly = false;

    private List<String> excludePrefixes = new ArrayList<>(Arrays.asList(
            "APPDATA", "CommonProgramFiles", "HOMEDRIVE", "HOMEPATH", 
            "LOCALAPPDATA", "ProgramData", "ProgramFiles", "PUBLIC",
            "SystemDrive", "SystemRoot", "TEMP", "TMP", "windir",
            "OneDrive", "PROCESSOR_", "NUMBER_OF_PROCESSORS",
            "OS", "PATHEXT", "PROMPT", "PSModulePath", "USERDOMAIN"
    ));

    private List<String> includePatterns = new ArrayList<>(Arrays.asList(
            "JAVA_", "MAVEN_", "GRADLE_", "SPRING_", "SERVER_", 
            "DATABASE_", "DB_", "REDIS_", "KAFKA_", "AWS_",
            "AZURE_", "GCP_", "PORT", "HOST", "PATH",
            "CLASSPATH", "NODE_", "NPM_", "PYTHON_"
    ));

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

    public List<String> getExcludePrefixes() {
        return excludePrefixes;
    }

    public void setExcludePrefixes(List<String> excludePrefixes) {
        this.excludePrefixes = excludePrefixes;
    }

    public List<String> getIncludePatterns() {
        return includePatterns;
    }

    public void setIncludePatterns(List<String> includePatterns) {
        this.includePatterns = includePatterns;
    }
}