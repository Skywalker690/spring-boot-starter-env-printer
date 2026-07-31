# 🌍 Spring Boot Starter Env Printer

[![Maven Central](https://img.shields.io/maven-central/v/io.github.skywalker690/spring-boot-starter-env-printer)](https://central.sonatype.com/artifact/io.github.skywalker690/spring-boot-starter-env-printer)
[![GitHub Release](https://img.shields.io/github/v/release/Skywalker690/spring-boot-starter-env-printer)](https://github.com/Skywalker690/spring-boot-starter-env-printer/releases)
[![Java Version](https://img.shields.io/badge/Java-17%2B-blue.svg)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2%2B-brightgreen.svg)]()
[![License](https://img.shields.io/badge/License-Apache%202.0-green.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-Welcome-blueviolet.svg)](CONTRIBUTING.md)
[![Stars](https://img.shields.io/github/stars/Skywalker690/spring-boot-starter-env-printer?style=social)](https://github.com/Skywalker690/spring-boot-starter-env-printer/stargazers)

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
  <groupId>io.github.skywalker690</groupId>
  <artifactId>spring-boot-starter-env-printer</artifactId>
  <version>2.0.0</version>
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
  <version>2.0.0</version>
</dependency>
```

### 2️⃣ Run Your Application

Environment variables will automatically be printed at startup (OS-specific variables excluded):

```
2025-10-28T14:00:07.184Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : ===============================
2025-10-28T14:00:07.184Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : 🌍 Environment Variables
2025-10-28T14:00:07.185Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : ===============================
2025-10-28T14:00:07.188Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : DATABASE_URL
2025-10-28T14:00:07.189Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : JAVA_HOME
2025-10-28T14:00:07.190Z  INFO --- [main] com.skywalker.envprinter.EnvPrinter : SERVER_PORT
...
```

### 3️⃣ Access via HTTP Endpoint

Endpoints are disabled by default. Set `env.printer.endpoint-enabled=true` to enable them, and protect them with authentication before exposing them.

```bash
# Actuator-independent endpoint (works without Spring Boot Actuator)
curl http://localhost:8080/env/env-printer

# Or if you have Spring Boot Actuator
curl http://localhost:8080/actuator/envprinter
```

Returns:
```json
{
  "DATABASE_URL": "",
  "JAVA_HOME": "",
  "SERVER_PORT": ""
}
```

## ⚙️ Configuration

### Basic Configuration

```properties
# Enable or disable the env printer (default: true)
env.printer.enabled=true

# Enable or disable the HTTP endpoint (default: false)
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
- Detects `@Value("${ENV_VAR}")` annotations and environment-variable literals in compiled classes (best effort)

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

# Show env values
env.printer.show-values=true

spring.application.name=my-spring-app
```

**YAML Configuration:**
```yaml
env:
  printer:
    enabled: true
    endpoint-enabled: true
    project-only: true
    show-values: false

```

## 🛠️ Configuration Properties Reference

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `env.printer.enabled` | Boolean | `true` | Enable/disable the environment printer. When disabled, no environment variables will be printed at startup and the endpoint will not be available. |
| `env.printer.endpoint-enabled` | Boolean | `false` | Enable/disable the HTTP endpoints. Disabled by default to avoid exposing configuration metadata. |
| `env.printer.project-only` | Boolean | `true` | Show only variables referenced in configuration files and compiled classes. |

## 📊 Endpoints

### `/env/env-printer` (REST Controller, opt-in)
- **Type**: Standard REST endpoint
- **Requires**: Spring Web (no actuator needed)
- **Access**: `GET http://localhost:8080/env/env-printer`
- **Response**: JSON map of filtered environment variables

### `/actuator/envprinter` (Actuator Endpoint, opt-in)
- **Type**: Spring Boot Actuator endpoint
- **Requires**: Spring Boot Actuator dependency
- **Access**: `GET http://localhost:8080/actuator/envprinter`
- **Response**: JSON map of filtered environment variables
- **Note**: Requires actuator endpoints to be exposed

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
The HTTP endpoint is disabled by default. If enabled, protect it with authentication and authorization; `show-values=true` can expose secrets.

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
   - `@Value("${VARIABLE_NAME}")` annotations
   - Environment-variable literals in compiled classes (best effort)

3. **Filters environment**:
   - Shows only variables found in step 2
   - Excludes hardcoded OS variables
   - Returns sorted alphabetically

**Performance:** Scanning happens once at startup and results are cached.

---

## 📄 License

Apache License 2.0

---

**Author:** Sanjo (Skywalker)  
**Repository:** [spring-boot-starter-env-printer](https://github.com/Skywalker690/spring-boot-starter-env-printer)
