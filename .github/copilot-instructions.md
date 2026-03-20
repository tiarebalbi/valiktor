# GitHub Copilot Instructions for Valiktor

## Project Overview

Valiktor is a type-safe, powerful, and extensible fluent DSL to validate objects in Kotlin. It is a multi-module Gradle project written entirely in Kotlin, targeting JVM compatibility with Java 1.6+.

## Repository Structure

```
valiktor/
├── valiktor-core/                    # Core validation framework (main DSL, constraints, i18n)
├── valiktor-test/                    # Testing utilities for users of the library
├── valiktor-javatime/                # Validators for Java 8+ time API (java.time.*)
├── valiktor-jodatime/                # Validators for Joda-Time
├── valiktor-javamoney/               # Validators for JSR 354 Money and Currency
├── valiktor-jodamoney/               # Validators for Joda-Money
├── valiktor-spring/
│   ├── valiktor-spring/              # Core Spring Framework integration
│   ├── valiktor-spring-boot-autoconfigure/  # Spring Boot auto-configuration
│   └── valiktor-spring-boot-starter/        # Spring Boot starter
├── valiktor-samples/                 # Sample projects demonstrating usage
├── build.gradle.kts                  # Root Gradle build (shared configuration for all subprojects)
└── settings.gradle.kts               # Declares all subproject modules
```

Each module follows the standard Kotlin/JVM source layout:
```
<module>/src/main/kotlin/org/valiktor/   # Production source files
<module>/src/test/kotlin/org/valiktor/   # Test source files
<module>/src/main/resources/             # Resource files (e.g., i18n .properties)
<module>/src/test/resources/             # Test resource files
<module>/build.gradle.kts               # Module-specific dependencies
```

## Build System

**Build tool:** Gradle with Kotlin DSL (`.kts` files)

### Common Build Commands

```bash
# Full clean build with tests
./gradlew clean build

# Build without tests
./gradlew build -x test

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :valiktor-core:test

# Run linting checks (ktlint)
./gradlew lintKotlin

# Auto-fix lint issues
./gradlew formatKotlin

# Generate documentation (KDoc via Dokka)
./gradlew dokka

# Generate JaCoCo coverage report
./gradlew jacocoTestReport
```

### Important Notes
- Always use `./gradlew` (the Gradle wrapper) — never install or invoke a global `gradle` command.
- The root `build.gradle.kts` applies shared configuration to all subprojects. Module-specific configuration lives in each module's own `build.gradle.kts`.
- JVM target is set to `1.6` for maximum compatibility.
- Code coverage minimum is enforced at 30% (JaCoCo).

## Testing

- **Framework:** JUnit 5 (JUnit Jupiter) via `kotlin("test-junit5")`
- **Assertions:** AssertJ (`org.assertj:assertj-core`)
- **Test activation:** `useJUnitPlatform()` in each module's Gradle config

### Test Conventions

- Test class names end with `Test` (e.g., `AnyConstraintsTest`, `StringFunctionsTest`)
- Test method names use backtick-enclosed descriptive strings (Kotlin idiomatic): `` fun `should validate null value`() ``
- Tests for constraint messages validate interpolated messages for **all supported locales**
- Test files mirror the package structure of the source under test

### Example Test Pattern

```kotlin
package org.valiktor.constraints

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.valiktor.i18n.SupportedLocales
import org.valiktor.i18n.interpolatedMessages
import kotlin.test.Test

class NullTest {
    @Test
    fun `should validate messages`() {
        assertThat(Null.interpolatedMessages()).containsExactly(
            entry(SupportedLocales.DEFAULT, "Must be null"),
            entry(SupportedLocales.EN, "Must be null")
        )
    }
}
```

## Coding Conventions

- **Language:** Kotlin only — no Java source files in the main or test source sets
- **Style:** Enforced by [ktlint](https://ktlint.github.io/) via the `org.jmailen.kotlinter` Gradle plugin (version 3.2.0)
  - Disabled rule: `import-ordering`
- **Naming:**
  - Classes: `PascalCase` (e.g., `StringFunctions`, `AnyConstraints`)
  - Functions/properties: `camelCase`
  - Test methods: backtick strings with descriptive names
- **License headers:** All source files must include the Apache License 2.0 header
- **No `var`**: Prefer `val` (immutable) over `var` wherever possible
- **No nulls**: Prefer Kotlin null-safety over nullable types where reasonable

## Core Architecture

### DSL Entry Point

```kotlin
import org.valiktor.validate

data class Employee(val name: String, val age: Int)

validate(employee) {
    validate(Employee::name).hasSize(min = 3, max = 100).isNotBlank()
    validate(Employee::age).isPositive().isLessThanOrEqualTo(120)
}
```

### Key Interfaces

- `Constraint` — base interface for all validation constraints (in `valiktor-core`)
- `ConstraintViolation` — represents a single validation failure (property path, value, constraint)
- `Validator<E>` — DSL builder class that collects constraint violations

### Adding a New Constraint

1. Add the constraint object/class to the appropriate file in `constraints/` (e.g., `TextConstraints.kt`)
2. Add the corresponding extension function to the appropriate `functions/` file (e.g., `StringFunctions.kt`)
3. Add i18n message keys to **all** supported locale `.properties` files under `src/main/resources/org/valiktor/`
4. Write a test in the corresponding `*Test.kt` file covering constraint messages for all locales

### Supported Locales (i18n)

Locale resource files must be updated when adding new constraint messages:
- `messages.properties` (default / English fallback)
- `messages_en.properties`
- `messages_ca.properties` (Catalan)
- `messages_de.properties` (German)
- `messages_es.properties` (Spanish)
- `messages_ja.properties` (Japanese)
- `messages_pt_BR.properties` (Brazilian Portuguese)

## CI/CD Pipeline

Defined in `.github/workflows/`:

- **`ci.yml`** — Runs on every push and pull request; builds and tests against JDK 8 and JDK 11; uploads coverage to Codecov (JDK 8 only)
- **`publish.yml`** — Triggered on version tags (`v*`); builds, then publishes artifacts to Maven Central via Sonatype OSSRH

## Dependencies

All dependency versions are declared as `val` variables in the root `build.gradle.kts`. When updating a dependency, change the version there. Module-level `build.gradle.kts` files reference those variables.

Key dependencies:
- `kotlin("stdlib")` — Kotlin standard library
- `org.junit.jupiter:junit-jupiter-engine` — JUnit 5 runtime
- `org.assertj:assertj-core` — Fluent assertions in tests

## What to Avoid

- Do **not** add Java source files — this is a pure Kotlin project
- Do **not** introduce new Gradle plugins without updating the root `build.gradle.kts`
- Do **not** skip i18n message files when adding new constraints — all locales must be updated together
- Do **not** lower the JaCoCo minimum coverage threshold
- Do **not** use `var` when `val` is sufficient
- Do **not** use `!!` (non-null assertion) — handle nullability explicitly
