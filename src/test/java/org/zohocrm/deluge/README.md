# Deluge Plugin Test Suite

This directory contains comprehensive tests for the Zoho CRM Deluge Language Support plugin.

## Test Structure

### Unit Tests

1. **DelugeFileTypeTest.java**
   - Tests file type recognition for `.deluge` extension
   - Verifies that `.js` files are NOT recognized as Deluge
   - Tests singleton pattern and default extension
   - Tests various file name patterns

2. **DelugeLanguageTest.java**
   - Tests language registration and ID
   - Verifies singleton pattern
   - Tests language discovery by ID

3. **DelugeParserDefinitionTest.java**
   - Tests lexer creation
   - Tests parser creation
   - Verifies token sets (whitespace, comments, strings)
   - Tests file node type

4. **DelugeFileTest.java**
   - Tests Deluge file creation and manipulation
   - Tests file content with functions, comments, and various syntax
   - Verifies file type and language association
   - Ensures JavaScript files are not treated as Deluge

5. **DelugeTemplateContextTypeTest.java**
   - Tests template context availability
   - Verifies context in different code locations (functions, try-catch, etc.)

### Integration Tests

6. **DelugePluginIntegrationTest.java**
   - End-to-end workflow testing
   - Tests complete file lifecycle
   - Verifies only `.deluge` extension is recognized
   - Tests automation, validation, and standalone function structures
   - Tests multiple files in a project
   - Tests lexer handling of complex Deluge syntax

## Test Data Files

Located in `src/test/resources/testData/`:

- **standalone.deluge** - Sample standalone function
- **automation.deluge** - Sample automation function
- **validation.deluge** - Sample validation rule function

## Running Tests

### Run all tests:
```bash
./gradlew test
```

### Run specific test class:
```bash
./gradlew test --tests DelugeFileTypeTest
./gradlew test --tests DelugePluginIntegrationTest
```

### Run tests with verbose output:
```bash
./gradlew test --info
```

### Generate test report:
```bash
./gradlew test
# Report will be at: build/reports/tests/test/index.html
```

## Test Coverage

The test suite covers:

✅ **File Type Recognition**
- `.deluge` files are recognized
- `.js` files are NOT recognized as Deluge
- Other file types are NOT recognized as Deluge

✅ **Language Support**
- Language registration
- Language ID and display name
- Singleton pattern implementation

✅ **Parser and Lexer**
- Lexer creation and functionality
- Parser definition
- Token recognition

✅ **PSI (Program Structure Interface)**
- File creation and manipulation
- Content parsing
- File type and language association

✅ **Live Templates**
- Template context availability
- Context in various code locations

✅ **Integration**
- Complete workflow from file creation to parsing
- Multiple files in a project
- Complex Deluge syntax handling

## Test Assertions

Each test verifies:
1. Objects are not null
2. Correct types/instances
3. Expected values match
4. Singleton patterns work correctly
5. File extensions are properly filtered

## Extension Verification

The tests specifically verify that:
- **Only `.deluge` extension is recognized**
- `.js` files are explicitly NOT treated as Deluge files
- Other extensions (`.java`, `.py`, `.txt`, `.xml`) are NOT recognized

This ensures the plugin ONLY activates for `.deluge` files as required.

## Continuous Integration

These tests are designed to run in CI/CD pipelines and will fail if:
- `.js` files are incorrectly recognized as Deluge
- Any file type other than `.deluge` is treated as Deluge
- Core plugin functionality is broken

## Adding New Tests

When adding new tests:
1. Create test class in `src/test/java/org/zohocrm/deluge/`
2. Extend `BasePlatformTestCase`
3. Add test data files to `src/test/resources/testData/` if needed
4. Document the test purpose in this README
5. Run `./gradlew test` to verify

## Troubleshooting

If tests fail:
1. Check that IntelliJ Platform version matches (see `build.gradle`)
2. Ensure `plugin.xml` only lists `extensions="deluge"` (not `extensions="deluge;js"`)
3. Verify test data files exist in `testData/` directory
4. Run with `--info` flag for detailed output

