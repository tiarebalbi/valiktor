# Valiktor – Copilot Coding Agent Guidelines

## Project Overview

Valiktor is a **type-safe, powerful, and extensible fluent DSL for validating Kotlin objects**. It is a
fork of the original [valiktor/valiktor](https://github.com/valiktor/valiktor) project, with the main goals
of supporting **Kotlin 2.x** and **Spring Boot 4.x**.

When validation fails, a `ConstraintViolationException` is thrown containing a set of `ConstraintViolation`
objects that describe every property that failed, the invalid value, and the violated constraint.

---

## Language and Build System

- **Language:** Kotlin (targeting JVM)
- **Build system:** Gradle with Kotlin DSL (`build.gradle.kts`, `settings.gradle.kts`)
- **License:** Apache License 2.0 — every source file **must** include the Apache 2.0 license header (see below)

### Common Gradle Commands

```bash
./gradlew build                          # Full build (compile + test + lint)
./gradlew test                           # Run all tests
./gradlew :valiktor-core:test            # Run tests for a specific module
./gradlew ktlint                         # Check code style with ktlint
./gradlew ktlintFormat                   # Auto-format code with ktlint
./gradlew jacocoTestReport               # Generate coverage reports
./gradlew jacocoTestCoverageVerification # Verify coverage (minimum 30%)
./gradlew dokka                          # Generate KDoc/Javadoc
./gradlew clean                          # Clean build artifacts
```

---

## Module Structure

```
valiktor/
├── valiktor-core/              # Core validation engine — start here for most changes
├── valiktor-javatime/          # JSR 310 (java.time.*) support
├── valiktor-javamoney/         # JSR 354 (MonetaryAmount) support
├── valiktor-jodatime/          # Joda Time support
├── valiktor-jodamoney/         # Joda Money support
├── valiktor-spring/
│   ├── valiktor-spring/                       # Core Spring exception-handler integration
│   ├── valiktor-spring-boot-autoconfigure/    # Spring Boot auto-configuration
│   └── valiktor-spring-boot-starter/          # Convenience starter POM
├── valiktor-test/              # Testing utilities (shouldFailValidation DSL)
└── valiktor-samples/           # Runnable example applications
```

**Module dependency rule:** Extension modules depend on `valiktor-core`. They must **not** be circular.
New type-specific constraints or formatters belong in the appropriate extension module, not in core.

---

## Package Conventions

All production code lives under `org.valiktor.*`:

| Sub-package | Contents |
|---|---|
| `org.valiktor` | `Constraint`, `ConstraintViolation`, `Validator` |
| `org.valiktor.constraints` | Constraint data classes / objects |
| `org.valiktor.functions` | Extension functions for each type |
| `org.valiktor.i18n` | Message loading, formatting, interpolation |
| `org.valiktor.i18n.formatters` | Type-specific `Formatter` implementations |
| `org.valiktor.springframework.*` | Spring/Spring Boot integration |
| `org.valiktor.test` | Test DSL utilities |

Extension modules mirror this structure under the same root package.

---

## License Header

Every Kotlin source file **must** begin with this header:

```kotlin
/*
 * Copyright 2018-2020 https://www.valiktor.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```

---

## Code Style

- **Formatter:** [ktlint](https://ktlint.github.io) is enforced via the `org.jmailen.kotlinter` Gradle plugin.
  - Run `./gradlew ktlintFormat` to auto-fix style issues before committing.
  - The `import-ordering` rule is disabled (see `build.gradle.kts`).
- Follow the official [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html).
- **Line length:** ktlint default (~120 characters).
- **Indentation:** 4 spaces (no tabs).
- Prefer Kotlin idiomatic constructs: extension functions, lambdas, data classes, `object` singletons.
- Avoid mutable state in constraints and formatters — they must be **stateless**.

---

## Naming Conventions

| Element | Convention | Examples |
|---|---|---|
| Constraint class / object | PascalCase | `NotEmpty`, `Email`, `Size`, `Between` |
| Validation extension function | camelCase, verb prefix | `isNotEmpty()`, `hasSize()`, `isEmail()` |
| Formatter object | PascalCase + `Formatter` suffix | `AnyFormatter`, `NumberFormatter` |
| Test class | Source class name + `Test` | `StringFunctionsTest`, `TextConstraintsTest` |
| Test method | backtick string, human-readable | `` `isNotEmpty with null should be valid` `` |
| Packages | lowercase, dot-separated | `org.valiktor.constraints` |

---

## Adding a New Constraint

1. **Define the constraint** in the appropriate `*Constraints.kt` file under
   `valiktor-core/src/main/kotlin/org/valiktor/constraints/`.
   - Use `object` for stateless (parameterless) constraints.
   - Use `data class` for parameterized constraints; each constructor parameter becomes a message variable.

   ```kotlin
   // Stateless constraint
   object MyConstraint : Constraint

   // Parameterized constraint
   data class MyRange<T>(val min: T, val max: T) : Constraint
   ```

2. **Add KDoc comments** for the constraint class and all properties:

   ```kotlin
   /**
    * Represents a constraint that validates if the value is within a custom range
    *
    * @property min specifies the minimum value (inclusive)
    * @property max specifies the maximum value (inclusive)
    *
    * @author Your Name
    * @see Constraint
    * @since 0.x.0
    */
   data class MyRange<T>(val min: T, val max: T) : Constraint
   ```

3. **Add i18n messages** to every locale properties file under
   `valiktor-core/src/main/resources/org/valiktor/`:
   - `messages.properties` (default — English fallback)
   - `messages_en.properties`
   - `messages_ca.properties`
   - `messages_de.properties`
   - `messages_es.properties`
   - `messages_ja.properties`
   - `messages_pt_BR.properties`

   Message keys follow the pattern `org.valiktor.constraints.<ConstraintName>.message`.
   Parameter placeholders use `{paramName}` matching the constraint's property names.

   ```properties
   org.valiktor.constraints.MyRange.message=Must be between {min} and {max}
   ```

4. **Write a constraint message test** in the corresponding `*ConstraintsTest.kt` file under
   `valiktor-core/src/test/kotlin/org/valiktor/constraints/`. Verify every supported locale:

   ```kotlin
   class MyRangeTest {
       @Test
       fun `should validate messages`() {
           assertThat(MyRange(1, 10).interpolatedMessages()).containsExactly(
               entry(SupportedLocales.DEFAULT, "Must be between 1 and 10"),
               entry(SupportedLocales.CA, "..."),
               entry(SupportedLocales.DE, "..."),
               entry(SupportedLocales.EN, "Must be between 1 and 10"),
               entry(SupportedLocales.ES, "..."),
               entry(SupportedLocales.JA, "..."),
               entry(SupportedLocales.PT_BR, "...")
           )
       }
   }
   ```

---

## Adding a New Validation Function

1. **Add the extension function** to the appropriate `*Functions.kt` file under
   `valiktor-core/src/main/kotlin/org/valiktor/functions/`.
   The function must return `Validator<E>.Property<T>` to support fluent chaining.

   ```kotlin
   /**
    * Validates if the [String] property starts with a given prefix
    *
    * @param prefix the expected prefix
    * @receiver the property to be validated
    * @return the same receiver property
    */
   fun <E> Validator<E>.Property<String?>.startsWith(prefix: String): Validator<E>.Property<String?> =
       this.validate(StartsWith(prefix)) { it == null || it.startsWith(prefix) }
   ```

2. **Null handling rule:** Treat `null` as **valid** by default (return `true` when `it == null`) — the
   caller can add `.isNotNull()` separately if a non-null value is required.

3. **Write function tests** in the corresponding `*FunctionsTest.kt` file. Each constraint should have at
   least two test cases: one that passes and one that fails.

   ```kotlin
   private object StringFunctionsFixture {
       data class Employee(val name: String? = null)
   }

   class StringFunctionsTest {

       @Test
       fun `startsWith with matching prefix should be valid`() {
           validate(Employee(name = "hello")) {
               validate(Employee::name).startsWith("hel")
           }
       }

       @Test
       fun `startsWith with non-matching prefix should be invalid`() {
           val exception = assertFailsWith<ConstraintViolationException> {
               validate(Employee(name = "world")) {
                   validate(Employee::name).startsWith("hel")
               }
           }
           assertThat(exception.constraintViolations).containsExactly(
               DefaultConstraintViolation(property = "name", value = "world", constraint = StartsWith("hel"))
           )
       }
   }
   ```

---

## Adding a New Formatter

Formatters control how constraint parameter values are rendered in i18n error messages.

1. Create a new `object` implementing `Formatter<T>` in
   `valiktor-core/src/main/kotlin/org/valiktor/i18n/formatters/`:

   ```kotlin
   object MyTypeFormatter : Formatter<MyType> {
       override fun format(value: MyType, messageBundle: MessageBundle): String =
           value.toString() // or a locale-aware rendering
   }
   ```

2. Register the formatter in the `FormatterSpi` service-provider file for the relevant module. For the
   core module, update `MessageFormatter.kt` or its companion `FormatterSpi` registration.

---

## i18n Guidelines

- Messages live in `.properties` files at `src/main/resources/org/valiktor/messages*.properties`.
- **Default locale** (`messages.properties`) must always be present and contain English text.
- Unicode characters (e.g., Japanese) must be stored as Unicode escape sequences (`\uXXXX`) because
  Gradle filters these files through `EscapeUnicode` during processing.
- Use `{paramName}` placeholders that match the constraint's Kotlin property names exactly.
- All **7 supported locales** must be updated whenever a new constraint is added:
  `DEFAULT`, `EN`, `CA`, `DE`, `ES`, `JA`, `PT_BR`.

---

## Testing Guidelines

### Frameworks

- **JUnit 5** (Jupiter) with `@Test` annotations via `kotlin.test`
- **AssertJ** for fluent assertions (`assertThat(...)`)
- **`assertFailsWith<ConstraintViolationException>`** for negative tests

### Test File Location

Mirror the source file structure:

```
src/main/kotlin/org/valiktor/constraints/TextConstraints.kt
  → src/test/kotlin/org/valiktor/constraints/TextConstraintsTest.kt

src/main/kotlin/org/valiktor/functions/StringFunctions.kt
  → src/test/kotlin/org/valiktor/functions/StringFunctionsTest.kt
```

### Test Patterns

**Constraint message test** (one `@Test` per constraint, all locales):
```kotlin
class NotBlankTest {
    @Test
    fun `should validate messages`() {
        assertThat(NotBlank.interpolatedMessages()).containsExactly(
            entry(SupportedLocales.DEFAULT, "Must not be blank"),
            entry(SupportedLocales.CA, "No pot estar en blanc"),
            entry(SupportedLocales.DE, "Darf nicht blank sein"),
            entry(SupportedLocales.EN, "Must not be blank"),
            entry(SupportedLocales.ES, "No puede estar vacío"),
            entry(SupportedLocales.JA, "空文字以外である必要があります"),
            entry(SupportedLocales.PT_BR, "Não deve estar em branco")
        )
    }
}
```

**Function test** (one valid + one invalid case per function):
```kotlin
@Test
fun `isNotEmpty with not-empty string should be valid`() {
    validate(Employee(name = "John")) {
        validate(Employee::name).isNotEmpty()
    }
}

@Test
fun `isNotEmpty with empty string should be invalid`() {
    val exception = assertFailsWith<ConstraintViolationException> {
        validate(Employee(name = "")) {
            validate(Employee::name).isNotEmpty()
        }
    }
    assertThat(exception.constraintViolations).containsExactly(
        DefaultConstraintViolation(property = "name", value = "", constraint = NotEmpty)
    )
}
```

**Integration test using the valiktor-test DSL:**
```kotlin
shouldFailValidation<Employee> {
    Employee(id = -1, name = "ab", email = "bad")
}.verify {
    expect(Employee::id, -1, Greater(0))
    expect(Employee::name, "ab", Size(min = 3, max = 80))
    expect(Employee::email, "bad", Email)
}
```

### Coverage

- Minimum code coverage enforced at **30%** via JaCoCo.
- Aim for complete coverage of all constraint paths, especially `null` handling and boundary values.

---

## Validation DSL Usage Patterns

### Basic Object Validation

```kotlin
validate(employee) {
    validate(Employee::id).isPositive()
    validate(Employee::name).hasSize(min = 3, max = 80)
    validate(Employee::email).isNotBlank().isEmail()
}
```

### Nested Object Validation

```kotlin
validate(employee) {
    validate(Employee::address) {
        validate(Address::street).isNotBlank()
        validate(Address::city).isNotBlank()
    }
}
```

### Collection Validation

```kotlin
validate(employee) {
    validate(Employee::emails).hasSize(min = 1)
    validate(Employee::emails).forEach {
        validate(Email::value).isEmail()
    }
}
```

### Coroutine-based (Suspend) Validation

```kotlin
validate(employee) {
    coValidate(Employee::email) { email ->
        emailService.isUnique(email) // suspend function
    }
}
```

### Inline in `init` Block

```kotlin
data class Employee(val id: Int, val name: String, val email: String) {
    init {
        validate(this) {
            validate(Employee::id).isPositive()
            validate(Employee::name).hasSize(min = 3, max = 80)
            validate(Employee::email).isNotBlank().isEmail()
        }
    }
}
```

---

## Spring Integration Guidelines

- Exception handlers live in `org.valiktor.springframework.http`.
- Separate WebMvc and WebFlux implementations exist — changes to one may need to be mirrored in the other.
- Spring Boot auto-configuration lives in `valiktor-spring-boot-autoconfigure`.
- Use `ValiktorConfiguration` to customize the message bundle name in Spring contexts.

---

## KDoc Documentation

All public APIs must have KDoc comments. Required tags:

```kotlin
/**
 * Brief description of what this constraint / function does.
 *
 * @property paramName description of parameter (for constraint data classes)
 * @param paramName description of parameter (for functions)
 * @receiver the property to be validated (for extension functions)
 * @return the same receiver property (for extension functions)
 *
 * @author Your Name
 * @see RelatedClass
 * @since 0.x.0
 */
```

---

## Common Pitfalls to Avoid

1. **Do not fail on `null`** — validation functions should return `true` (valid) when the value is `null`.
   Null-checking is done separately with `isNotNull()` or `isNull()`.
2. **Do not add mutable state** to constraints or formatters — they are shared instances.
3. **Do not skip message properties** — every new constraint needs messages in all 7 locale files.
4. **Do not break the fluent chain** — extension functions must always return `Validator<E>.Property<T>`.
5. **Do not add hard-coded locale strings** — always use `SupportedLocales.*` constants in tests.
6. **Do not add dependencies** to `valiktor-core` that are not strictly necessary; keep the core lean.
