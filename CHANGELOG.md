# Changelog

All notable changes to this project will be documented in this file.


## [2.1.0] - 2026-08-03

### Improved
- Scope compiled-class scanning to explicitly configured application packages.
- Disable compiled-class scanning by default to avoid dependency false positives and startup overhead.
- Make project-scan caching thread-safe.
- Add configuration metadata for scanner and value-display properties.
- Upgrade the Central Publishing Maven plugin to 0.10.0.

### Added
- Java 17/21 CI workflow and Dependabot configuration.
- Auto-configuration integration tests and dependency-convergence enforcement.

## [2.0.0] - 2026-07-31

### Breaking changes
- HTTP and Actuator endpoints are disabled by default.
- Project-only filtering is enabled by default.
- Environment-variable detection now inspects compiled classes.

### Added
- Automated tests for environment scanning and endpoint masking.
- Safer optional Web and Actuator auto-configuration guards.

## [1.0.0] - 2025-10-28

### Added
- Initial release of Spring Boot Starter Env Printer
- Automatic environment variable printing at application startup using SLF4J logging
- Actuator-independent REST endpoint at `/env/env-printer`
- Optional Spring Boot Actuator endpoint at `/actuator/envprinter`
- Project-only mode (`env.printer.project-only=true`) that scans project files to show only variables actually used
- Intelligent project scanning:
  - Scans `application.properties` and `application.yml` files
  - Detects `${ENV_VAR}` placeholders
  - Detects `@Value("${ENV_VAR}")` annotations
  - Detects `System.getenv("VAR")` calls
- Hardcoded filtering of 50+ common OS-specific environment variables
  - Windows: APPDATA, TEMP, TMP, windir, SystemRoot, etc.
  - Linux/Unix: DISPLAY, XDG_*, SHELL, TERM, locale variables
- Automatic trimming of common variable name prefixes (env., environment., sys., system.)
- Enhanced logging showing both variable names and values
- Missing variables visibility (unset variables appear with `null` in endpoint response)
- Configuration properties with Spring Boot configuration metadata
- Sorted alphabetical output for easy reading
- Zero-configuration setup with sensible defaults
- Spring Boot 3 compatibility

### Fixed
- Critical bug where `EnvPrinterEndpoint` used `@Component` instead of conditional bean registration
- Replaced `System.out.println` with proper SLF4J Logger
- Fixed endpoint ID warning by changing 'env-printer' to 'envprinter' for actuator endpoint

### Documentation
- Comprehensive README with features, usage, and examples
- Contributing guidelines (CONTRIBUTING.md)
- MIT License (LICENSE at the time of the 1.0.0 release)
- Complete documentation in docs/ folder
- Publishing guide for Maven Central and GitHub Packages


### Planned Features
- Additional configuration options for custom filtering
- Support for more environment variable patterns
- Integration with Spring Cloud Config
- Support for environment variable validation

---


