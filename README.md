# Kotlin/JS Serialization Bug with `kotlin.js.ir.output.granularity=whole-program`

## Summary

When using `kotlin.js.ir.output.granularity=whole-program` in a Kotlin/JS project with kotlinx.serialization, tests fail on subsequent runs with `SerializationException`. The first test run usually passes, but all subsequent runs fail.

## Environment

- **Kotlin**: 2.3.10
- **kotlinx.serialization**: 1.10.0
- **Gradle**: 9.x
- **OS**: Windows 11 (also likely reproducible on other platforms)

## Steps to Reproduce

1. Clone this repository
2. Run the tests multiple times in succession:

**Windows PowerShell:**
```powershell
for ($i = 1; $i -le 5; $i++) { 
    Write-Host "Run $i:"; 
    ./gradlew jsTest --rerun-tasks 
}
```

**Bash:**
```bash
for i in {1..5}; do
  echo "Run $i:"
  ./gradlew jsTest --rerun-tasks
done
```

## Expected Behavior

All 5 test runs should pass consistently.

## Actual Behavior

- **Run 1**: PASSES
- **Run 2-5**: FAIL with `SerializationException`

Error output:
```
SerializationException at Platform.kt:50
```

## Workaround

Comment out or remove the `kotlin.js.ir.output.granularity=whole-program` line from `gradle.properties`:

```properties
# kotlin.js.ir.output.granularity=whole-program
```

With this line commented out, all tests pass consistently on every run.

## Analysis

The `whole-program` granularity setting compiles the entire Kotlin/JS project into a single `.js` file, which eliminates the need for bundlers like webpack. However, this mode appears to cause issues with how kotlinx.serialization discovers and registers serializers at runtime.

**Key observations:**
1. The first test run passes (serializers are freshly compiled/registered)
2. Subsequent runs fail - the serialization runtime cannot find the serializers
3. This is NOT test-framework-specific - it affects both `kotlin.test` and Kotest
4. The issue is in the interaction between Kotlin/JS whole-program compilation and kotlinx.serialization

## Files in this Reproduction

- `src/jsMain/kotlin/.../AugmentCliSessionDto.kt` - Data classes with `@Serializable` annotations
- `src/jsTest/kotlin/.../AugmentCliSessionDtoTest.kt` - Tests that deserialize JSON to these classes
- `gradle.properties` - Contains the problematic `kotlin.js.ir.output.granularity=whole-program` setting

