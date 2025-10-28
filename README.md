# 🌍 Spring Boot Starter Env Printer

A lightweight custom Spring Boot starter designed to automatically print environment variables at application startup — and optionally expose them via HTTP endpoint for easier debugging and configuration visibility.

This starter is useful for developers who deploy applications across multiple environments (like local, staging, or production) and need quick insight into what environment variables are available during runtime — without manually logging or debugging them.

## ✨ Features

- 🚀 **Auto-prints environment variables** at application startup with proper SLF4J logging
- 🌐 **Actuator-independent HTTP endpoint** at `/env/env-printer` - no actuator dependency required
- 🔍 **Optional actuator endpoint** at `/actuator/envprinter` when actuator is present
- 🎯 **Smart project-only mode** - scans your entire project to show only environment variables actually used
- 🚫 **Hardcoded OS exclusions** - automatically hides 50+ common OS-specific variables (APPDATA, TEMP, etc.)
- ⚙️ **Highly configurable** via `application.properties` or `application.yml`
- 📦 **Zero configuration required** - works out of the box with sensible defaults
- 🔤 **Sorted output** - environment variables are sorted alphabetically for easy reading
- 🎨 **Spring Boot 3 compatible** - uses modern autoconfiguration format

## 🚀 Quick Start

### 1️⃣ Add Dependency

```xml
<dependency>
  <groupId>com.skywalker</groupId>
  <artifactId>spring-boot-starter-env-printer</artifactId>
  <version>1.0.0</version>
</dependency>
```

Or from GitHub via JitPack:

```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.Skywalker690</groupId>
  <artifactId>spring-boot-starter-env-printer</artifactId>
  <version>1.0.0</version>
</dependency>
```

### 2️⃣ Run Your Application

Environment variables will automatically be printed at startup (OS-specific variables excluded):

```
2025-10-28T14:00:07.184Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : ===============================
2025-10-28T14:00:07.184Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : 🌍 Environment Variables
2025-10-28T14:00:07.185Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : ===============================
2025-10-28T14:00:07.188Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : DATABASE_URL = jdbc:postgresql://localhost:5432/mydb
2025-10-28T14:00:07.189Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : JAVA_HOME = /usr/lib/jvm/java-17
2025-10-28T14:00:07.190Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : SERVER_PORT = 8080
...
```

### 3️⃣ Access via HTTP Endpoint

```bash
# Actuator-independent endpoint (works without Spring Boot Actuator)
curl http://localhost:8080/env/env-printer

# Or if you have Spring Boot Actuator
curl http://localhost:8080/actuator/envprinter
```

Returns:
```json
{
  "DATABASE_URL": "jdbc:postgresql://localhost:5432/mydb",
  "JAVA_HOME": "/usr/lib/jvm/java-17",
  "SERVER_PORT": "8080"
}
```

## ⚙️ Configuration

### Basic Configuration

```properties
# Enable or disable the env printer (default: true)
env.printer.enabled=true

# Enable or disable the HTTP endpoint (default: true)
env.printer.endpoint-enabled=true
```

### 🎯 Project-Only Mode (Show Only Variables Used in Your Project)

Enable project-only mode to scan your entire project and show only environment variables that are actually referenced:

```properties
# Scan project files and show only used environment variables
env.printer.project-only=true
```

**What does it scan?**
- `application.properties` and `application.yml` files
- Finds `${ENV_VAR}` placeholders
- Detects `@Value("${ENV_VAR}")` annotations
- Detects `System.getenv("ENV_VAR")` calls

**Example:**

If your `application.yml` contains:
```yaml
database:
  url: ${DATABASE_URL}
  username: ${DB_USER}
  
server:
  port: ${SERVER_PORT:8080}
```

With `project-only=true`, only `DATABASE_URL`, `DB_USER`, and `SERVER_PORT` will be displayed (if they exist in the environment).

### Full Configuration Example

```properties
# Basic settings
env.printer.enabled=true
env.printer.endpoint-enabled=true

# Show only variables actually used in project
env.printer.project-only=true

spring.application.name=my-awesome-app
```

**YAML Configuration:**
```yaml
env:
  printer:
    enabled: true
    endpoint-enabled: true
    project-only: true
```

## 🛠️ Configuration Properties Reference

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `env.printer.enabled` | Boolean | `true` | Enable/disable the environment printer. When disabled, no environment variables will be printed at startup and the endpoint will not be available. |
| `env.printer.endpoint-enabled` | Boolean | `true` | Enable/disable the HTTP endpoint for environment variables. When disabled, only startup logging will occur. |
| `env.printer.project-only` | Boolean | `false` | Scan project files to show only environment variables actually referenced in configuration files and source code. When enabled, provides the most relevant view of your application's environment. |

## 📊 Endpoints

### `/env/env-printer` (REST Controller)
- **Type**: Standard REST endpoint
- **Requires**: Spring Web (no actuator needed)
- **Access**: `GET http://localhost:8080/env/env-printer`
- **Response**: JSON map of filtered environment variables

### `/actuator/envprinter` (Actuator Endpoint)
- **Type**: Spring Boot Actuator endpoint
- **Requires**: Spring Boot Actuator dependency
- **Access**: `GET http://localhost:8080/actuator/envprinter`
- **Response**: JSON map of filtered environment variables
- **Note**: Requires actuator endpoints to be exposed

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

## 📋 Requirements

- Java 17 or higher
- Spring Boot 3.2.4 or higher
- Spring Web (for REST controller endpoint)
- (Optional) Spring Boot Actuator for actuator endpoint

## 🎯 Use Cases

### 1. Development - See Only Variables Your App Uses
```properties
env.printer.project-only=true
```
Automatically scans and shows only `${...}` references from config files.

### 2. Debugging - See All Non-OS Variables
```properties
env.printer.project-only=false
```
Shows all environment variables except OS-specific ones.

### 3. Production - Disable Completely
```properties
env.printer.enabled=false
```

### 4. CI/CD - Verify Environment Setup
```properties
env.printer.enabled=true
env.printer.project-only=true
```
Check logs to verify all required variables are set.

## 🔒 Security Note

Be careful when exposing environment variables via HTTP endpoints in production. Consider:
Note: If using Spring Security don't forget about SecurityConfig to permit endpoint 

- Using `project-only=true` to limit exposure
- Securing endpoints with authentication
- Disabling in production with `env.printer.enabled=false`
- The starter automatically hides OS-specific variables

## 📝 How Project Scanning Works

When `project-only=true`:

1. **Scans configuration files** in classpath:
   - `application*.properties`
   - `application*.yml`, `application*.yaml`

2. **Extracts variable references**:
   - `${VARIABLE_NAME}` placeholders
   - `${VARIABLE_NAME:default}` with defaults
   - `@Value("${VARIABLE_NAME}")` annotations (limited in compiled code)
   - `System.getenv("VARIABLE_NAME")` calls (limited in compiled code)

3. **Filters environment**:
   - Shows only variables found in step 2
   - Excludes hardcoded OS variables
   - Returns sorted alphabetically

**Performance:** Scanning happens once at startup and results are cached.

## 📄 License

MIT

---

**Author:** Sanjo (Skywalker)  
**Repository:** [spring-boot-starter-env-printer](https://github.com/Skywalker690/spring-boot-starter-env-printer)
