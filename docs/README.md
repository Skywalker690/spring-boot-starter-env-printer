# Spring Boot Starter Env Printer - Documentation

Welcome to the Spring Boot Starter Env Printer documentation!

## Table of Contents

- [Installation](#installation)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [Features](#features)
- [API Reference](#api-reference)
- [Examples](#examples)
- [Troubleshooting](#troubleshooting)

## Installation

### Maven Central

Add the dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.skywalker690</groupId>
    <artifactId>spring-boot-starter-env-printer</artifactId>
    <version>1.0.0</version>
</dependency>
```


## Quick Start

1. Add the dependency to your Spring Boot project
2. Run your application
3. Environment variables will be automatically printed at startup

```
2025-10-28T15:00:00.000Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : ===============================
2025-10-28T15:00:00.000Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : 🌍 Environment Variables
2025-10-28T15:00:00.000Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : ===============================
2025-10-28T15:00:00.000Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : DATABASE_URL = jdbc:postgresql://localhost:5432/mydb
2025-10-28T15:00:00.000Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : JAVA_HOME = /usr/lib/jvm/java-17
...
```

## Configuration

All configuration properties use the prefix `env.printer`:

### Basic Configuration

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `env.printer.enabled` | Boolean | `true` | Enable or disable the env printer |
| `env.printer.endpoint-enabled` | Boolean | `true` | Enable or disable the HTTP endpoint |
| `env.printer.project-only` | Boolean | `false` | Show only variables used in project |

### Example Configuration

**application.properties:**
```properties
env.printer.enabled=true
env.printer.endpoint-enabled=true
env.printer.project-only=true
```

**application.yml:**
```yaml
env:
  printer:
    enabled: true
    endpoint-enabled: true
    project-only: true
```

## Features

### 1. Automatic Environment Variable Printing

Automatically prints all environment variables at application startup using SLF4J logging.

### 2. Project-Only Mode

When enabled, scans your project files to identify which environment variables are actually used:

- Scans `application.properties` and `application.yml`
- Detects `${ENV_VAR}` placeholders
- Detects `@Value("${ENV_VAR}")` annotations
- Detects `System.getenv("VAR")` calls

**Configuration:**
```properties
env.printer.project-only=true
```

### 3. HTTP Endpoints

Two endpoints are available for accessing environment variables via HTTP:

#### REST Controller Endpoint (Actuator-Independent)
- **Path:** `/env/env-printer`
- **Method:** GET
- **Requires:** Spring Web only (no actuator needed)

#### Actuator Endpoint (Legacy)
- **Path:** `/actuator/envprinter`
- **Method:** GET
- **Requires:** Spring Boot Actuator

**Example Response:**
```json
{
  "DATABASE_URL": "jdbc:postgresql://localhost:5432/mydb",
  "JAVA_HOME": "/usr/lib/jvm/java-17",
  "SERVER_PORT": "8080",
  "API_KEY": null
}
```

Note: Variables with `null` values are detected in your project but not set in the environment.



### 4. OS-Specific Variable Filtering

Automatically excludes 50+ common OS-specific environment variables:

**Windows:**
- APPDATA, TEMP, TMP, windir, SystemRoot, etc.

**Linux/Unix:**
- DISPLAY, XDG_*, SHELL, TERM, locale variables, etc.

This filtering is hardcoded and cannot be disabled.

## 🚫 Hardcoded OS Exclusions

The following OS-specific environment variables are automatically hidden (not configurable):

### Windows
- `APPDATA`, `CommonProgramFiles`, `HOMEDRIVE`, `HOMEPATH`
- `LOCALAPPDATA`, `ProgramData`, `ProgramFiles`, `PUBLIC`
- `SystemDrive`, `SystemRoot`, `TEMP`, `TMP`, `windir`
- `OneDrive`, `PROCESSOR_*`, `NUMBER_OF_PROCESSORS`
- `OS`, `PATHEXT`, `PROMPT`, `PSModulePath`, `USERDOMAIN`

### Linux/Unix
- `DISPLAY`, `SESSION_MANAGER`, `XDG_*` variables
- `SHELL`, `TERM`, `COLORTERM`, `SHLVL`
- Locale variables: `LANG`, `LANGUAGE`, `LC_*`

These exclusions keep the output focused on application-relevant variables.

### 5. Variable Name Trimming

Automatically trims common prefixes from variable names:

- `${env.DB_PASSWORD}` → `DB_PASSWORD`
- `${environment.API_KEY}` → `API_KEY`
- `${sys.CONFIG}` → `CONFIG`

## API Reference

### Configuration Properties Class

```java
@ConfigurationProperties(prefix = "env.printer")
public class EnvPrinterProperties {
    private boolean enabled = true;
    private boolean endpointEnabled = true;
    private boolean projectOnly = false;
    
    // Getters and setters...
}
```

### REST Controller

```java
@RestController
@RequestMapping("/env")
public class EnvPrinterController {
    @GetMapping("/env-printer")
    public Map<String, String> getEnvironmentVariables();
}
```

### Actuator Endpoint

```java
@Endpoint(id = "envprinter")
public class EnvPrinterEndpoint {
    @ReadOperation
    public Map<String, String> getEnvironment();
}
```

## Examples

### Example 1: Basic Usage

Simply add the dependency and run your application. Environment variables will be printed at startup.

### Example 2: Project-Only Mode

**application.yml:**
```yaml
database:
  url: ${DATABASE_URL}
  username: ${DB_USER}
  password: ${DB_PASSWORD}

server:
  port: ${SERVER_PORT:8080}

api:
  key: ${API_KEY}
```

**Configuration:**
```properties
env.printer.project-only=true
```

**Result:**
Only `DATABASE_URL`, `DB_USER`, `DB_PASSWORD`, `SERVER_PORT`, and `API_KEY` will be displayed.

### Example 3: Production Deployment

Disable the env printer in production:

```properties
# application-prod.properties
env.printer.enabled=false
```

Or disable only the endpoint:

```properties
# application-prod.properties
env.printer.endpoint-enabled=false
```


## Troubleshooting

### Issue: Environment variables not showing

**Solution:** Check that `env.printer.enabled=true` in your configuration.

### Issue: Endpoint returns 404

**Solution:** 
- For REST endpoint: Ensure Spring Web is on the classpath
- For Actuator endpoint: Ensure Spring Boot Actuator is on the classpath and endpoint is exposed

### Issue: Too many variables displayed

**Solution:** Enable project-only mode:
```properties
env.printer.project-only=true
```

### Issue: Variable names include prefixes

**Solution:** This is automatically handled. If you see prefixes like `env.`, ensure you're using version 1.0.0 or later.

### Issue: Missing variables in logs

**Solution:** Variables are logged at INFO level. Check your logging configuration.

## Support

- **Issues:** [GitHub Issues](https://github.com/Skywalker690/spring-boot-starter-env-printer1/issues)
- **Contributing:** See [CONTRIBUTING.md](../CONTRIBUTING.md)
- **License:** MIT License - see [LICENSE](../LICENSE)

## Version History

### 1.0.0 (Current)
- Initial release
- Project scanning functionality
- Actuator-independent endpoint
- OS-specific variable filtering
- Variable name trimming
- Enhanced logging with values
