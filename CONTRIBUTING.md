# Contributing to Spring Boot Starter Env Printer

Thank you for your interest in contributing to Spring Boot Starter Env Printer! We welcome contributions from the community.

## How to Contribute

### Reporting Issues

If you find a bug or have a suggestion for improvement:

1. Check if the issue already exists in the [Issues](https://github.com/Skywalker690/spring-boot-starter-env-printer1/issues) section
2. If not, create a new issue with:
   - A clear, descriptive title
   - Detailed description of the problem or suggestion
   - Steps to reproduce (for bugs)
   - Expected vs actual behavior
   - Your environment details (Spring Boot version, Java version, OS)

### Submitting Pull Requests

1. **Fork the repository** and create your branch from `main`
   ```bash
   git checkout -b feature/my-new-feature
   ```

2. **Make your changes**
   - Follow the existing code style
   - Add comments for complex logic
   - Keep changes focused and atomic

3. **Test your changes**
   - Ensure the project builds successfully: `mvn clean install`
   - Test with a sample Spring Boot application

4. **Commit your changes**
   - Use clear, descriptive commit messages
   - Follow conventional commit format: `type: description`
   - Example: `feat: add custom filter configuration`

5. **Push to your fork**
   ```bash
   git push origin feature/my-new-feature
   ```

6. **Create a Pull Request**
   - Provide a clear description of your changes
   - Reference any related issues
   - Explain the motivation behind the changes

## Development Setup

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Git

### Building the Project

```bash
# Clone the repository
git clone https://github.com/Skywalker690/spring-boot-starter-env-printer1.git
cd spring-boot-starter-env-printer1

# Build the project
mvn clean install

# Install to local Maven repository
mvn install
```

### Testing Your Changes

Create a test Spring Boot application:

```bash
# Create a new Spring Boot project and add the dependency
```

Add to your test project's `pom.xml`:
```xml
<dependency>
    <groupId>com.skywalker</groupId>
    <artifactId>spring-boot-starter-env-printer</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Code Style

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods focused and concise
- Use SLF4J for logging

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Questions?

Feel free to open an issue for any questions or clarifications needed.
