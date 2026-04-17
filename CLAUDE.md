# CLAUDE.md

This file provides guidance to Claude Code when working with this repository.

## Project Overview

**deluge-intellij-plugin** is an IntelliJ IDEA plugin that provides language support for the Zoho Deluge scripting language. It includes syntax highlighting, code folding, live templates, real-time error detection, and Deluge coding standards enforcement (v2.3).

## Build & Development Commands

```bash
# Windows
gradlew.bat buildPlugin          # Build distributable ZIP
gradlew.bat runIde               # Launch a sandboxed IDE with the plugin loaded
gradlew.bat clean buildPlugin    # Clean rebuild

# Linux/macOS
./gradlew buildPlugin
./gradlew runIde
```

**Install locally:** `File → Settings → Plugins → Install Plugin from Disk` → select `build/distributions/deluge-intellij-plugin-1.0.1.zip`

CI runs on GitHub Actions (`.github/workflows/build.yml`) — triggers on push to `main` and version tags.

## Architecture

### Language Registration (`src/main/java/org/zohocrm/deluge/`)

- **`DelugeLanguage.java`** — Singleton language definition; ID = `"Deluge"`
- **`DelugeFileType.java`** — Registers `.deluge` and `.dg` extensions with the blue "D" icon
- **`DelugeParserDefinition.java`** — Wires lexer, parser, and PSI node types together

### Lexer & Parser

- **`DelugeLexer.flex`** — JFlex grammar; source of truth for all token definitions. Edit this to add/change token recognition.
- **`src/main/gen/org/zohocrm/deluge/DelugeLexer.java`** — Auto-generated from the `.flex` file via `generateDelugeLexer` Gradle task. Never edit manually.
- **`psi/DelugeTypes.java`** — Token type constants (157 tokens)
- **`Deluge.bnf`** — Grammar-Kit BNF grammar (used for parser generation reference)

### Editor Features

- **`DelugeSyntaxHighlighter.java`** — Maps token types to text attribute keys (colors)
- **`DelugeFoldingBuilder.java`** — Folds `{}` and `[]` blocks
- **`DelugeFormattingModelBuilder.java`** + **`DelugeBlock.java`** — Code formatter (Ctrl+Alt+L); 4-space indent
- **`DelugeLanguageCodeStyleSettingsProvider.java`** — Exposes formatting settings in `Settings → Editor → Code Style → Deluge`

### Annotators (real-time error/warning highlights)

- **`DelugeSyntaxAnnotator.java`** — Detects unmatched braces, parentheses, and brackets
- **`DelugeStandardsAnnotator.java`** — Enforces Deluge coding standards v2.3 (typed variable declarations, forbidden syntax, etc.)

### Standards & Live Templates

- **`standards/DelugeStandards.java`** — All coding standards rules as constants/sets
- **`src/main/resources/liveTemplates/Deluge.xml`** — 13 pre-built code snippets (standalone, automation, validation, schedule, button, relatedlist, try, log, foreach, ifnull, getrecord, updaterecord, pagination)

## Code Conventions

- Token types defined in `DelugeTypes.java`; new tokens require updating `DelugeLexer.flex` and re-running `generateDelugeLexer`
- Standards rules live in `DelugeStandards.java` — add new forbidden patterns or required conventions there
- Live templates use `$VARIABLE$` placeholders; `$END$` marks final cursor position

## Deluge Coding Standards

Follow `copilot/DELUGE_CODING_STANDARDS_v2.3.md` when generating or modifying Deluge code snippets or test files. Key rules: `snake_case` variables, mandatory `try-catch` on API calls, `info` for logging, `UPPER_SNAKE_CASE` constants, `verb_noun` function names.

## Testing

Tests are in `src/test/java/org/zohocrm/deluge/` and use IntelliJ's `BasePlatformTestCase`. They are disabled by default (`runTests = false` in `build.gradle`). Test data files are in `src/test/resources/testData/`.

Manual testing: use `gradlew.bat runIde` and open the `.deluge` demo files in the root directory.
