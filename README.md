# 🌍 Spring Boot Starter Env Printer

A lightweight Spring Boot starter that automatically prints all environment variables when your application starts.

## 🚀 How It Works

Just include the dependency in your project, and it will log all environment variables on startup.

### Example Output

```
===============================
🌍 Environment Variables
===============================
JAVA_HOME = /usr/lib/jvm/java-17-openjdk
USER = root
PATH = /usr/local/bin:/usr/bin:/bin
...
===============================
```

## ⚙️ Usage

### 1️⃣ Add Dependency

If published to Maven Central:

```xml
<dependency>
  <groupId>com.skywalker</groupId>
  <artifactId>spring-boot-starter-env-printer</artifactId>
  <version>1.0.0</version>
</dependency>
```

Or directly from GitHub via JitPack:

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

### 2️⃣ Enable/Disable via application.properties

```properties
env.printer.enabled=true
```

### 3️⃣ Run your app

When the app starts, it will automatically print all environment variables.

---

**Author:** Sanjo (Skywalker)  
**License:** MIT
