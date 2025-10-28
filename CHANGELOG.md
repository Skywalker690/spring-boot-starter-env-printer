# Changelog

All notable changes to this project will be documented in this file.


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
- MIT License (LICENSE)
- Complete documentation in docs/ folder
- Publishing guide for Maven Central and GitHub Packages


### Planned Features
- Additional configuration options for custom filtering
- Support for more environment variable patterns
- Integration with Spring Cloud Config
- Support for environment variable validation

---


