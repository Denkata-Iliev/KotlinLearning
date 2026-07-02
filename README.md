# Kotlin Learning Projects

Three progressive console applications designed to transition from "writing Java in Kotlin syntax" to thinking and writing idiomatic Kotlin.

---

## Project 1: 📊 Personal Expense Tracker

A menu-driven CLI application to log, categorize, and analyze personal expenses.

### Features
- Add, list, delete expenses
- Category summary with percentage breakdown
- Filter by date range (custom, this week/month/year)
- Search by description
- Full statistics dashboard (totals, averages, top expenses, monthly comparison)
- CSV file persistence
- Formatted table output

### Kotlin Concepts Learned
- `data class` for models (auto-generated `equals`, `hashCode`, `toString`, `copy`)
- `enum class` with properties (`Category` with labels)
- `when` expressions (menu handling, input validation)
- Collection operations (`groupBy`, `sumOf`, `filter`, `sortedByDescending`, `mapValues`, `joinToString`, `take`, `forEachIndexed`, `distinctBy`)
- Extension functions (`BigDecimal.asCurrency()`, `LocalDate.toDisplayDate()`, `List<BigDecimal>.average()`, `Array<Category>.printCategoryOptions()`)
- `readln()` instead of `Scanner`
- Null-safe parsing (`toIntOrNull()`, `toBigDecimalOrNull()`, `takeIf`)
- `buildString` for output construction
- `object` declarations for singletons (`CsvConverter`, `TableRenderer`)
- `runCatching { }.getOrNull()` for safe deserialization
- String templates and `trimIndent()` for multiline strings
- `File.writeText()` / `readLines()` for Kotlin-style file I/O

---

## Project 2: 📝 Note CLI System

A command-line note-taking app with parsed commands like `add "title" --tags=work`, `list --archived`, `delete 5 --force`.

### Features
- `add "title" [--tags=t1,t2] [--priority=N]` — create notes
- `list [--tags=...] [--priority=N] [--archived] [--all]` — list/filter notes
- `show <id>` — display note details
- `edit <id> "new title"` — update note title
- `tag <id> t1,t2` — replace tags on a note
- `delete <id> [--force]` — delete with confirmation
- `archive <id>` / `unarchive <id>` — archive management
- `search "query"` — case-insensitive search
- `help` — formatted help menu

### Kotlin Concepts Learned
- **Sealed classes** for command hierarchy (`sealed class Command` with `Add`, `Delete`, `ListAll`, `Edit`, etc.)
- **Sealed classes** for results (`ParseResult`, `CommandResult` — `Success`, `Error`, `Exit`)
- **Exhaustive `when`** — compiler-enforced handling of all command/result types
- **`data object`** for singleton sealed variants (`Help`, `Exit`, `Unknown`)
- Command pattern architecture (Parser → Executor → Result → Display)
- `object` singletons for `CommandParser`, `CommandExecutor`, `NoteRepo`
- Higher-order functions for deduplication:
  - `updateNote(id, transform: (Note) -> Note)` in the repository
  - `executeAndMapResult(message, action: () -> Boolean)` in the executor
  - `parseIdCommand(input, name, create: (Int) -> Command)` in the parser
- Constructor references (`Command::Archive`)
- `data class copy()` for immutable updates (`note.copy(archived = true)`)
- `buildList` for conditional column construction
- `takeIf` for index validation
- `also` for side-effect checks
- Regex with `Regex.find()`, `findAll()`, `groupValues` for command parsing
- `generateSequence` + `map` + `takeWhile` + `forEach` for the main REPL loop
- Separation of concerns (parser doesn't do I/O, executor doesn't format output)

### Architecture
```
Input → CommandParser.parse() → ParseResult
    → CommandExecutor.execute() → CommandResult
    → CommandResult.display()
```

---

## Project 3: 🧠 Quiz Engine with DSL

A quiz system featuring a Kotlin DSL for defining quizzes, a quiz runner, and timed questions using coroutines.

### Features
- **Kotlin DSL** for quiz definitions using lambdas with receivers
- Multiple choice and single choice questions
- Quiz runner with scoring, pass/fail, and feedback
- Timed questions using Kotlin coroutines
- Builder validation with `require()`
- `@DslMarker` for scope safety

### DSL Example
```kotlin
val myQuiz = quiz("Kotlin Fundamentals") {
    description = "Test your Kotlin knowledge!"
    passingScorePercent = 70

    question("What keyword replaces 'switch' in Kotlin?") {
        option("when", correct = true)
        option("match")
        option("case")
        option("select")
        explanation = "'when' is Kotlin's replacement for switch"
        points = 1
    }

    question("Which are scope functions?") {
        option("let", correct = true)
        option("apply", correct = true)
        option("use")
        option("also", correct = true)
        explanation = "'use' is for resource management"
        points = 2
    }
}
```

### Kotlin Concepts Learned
- **Lambdas with receivers** (`QuizBuilder.() -> Unit`) — the core DSL mechanic
- **Builder pattern via DSL** (`QuizBuilder`, `QuestionBuilder` with `build()` methods)
- `apply(init)` for executing builder lambdas
- **`@DslMarker`** annotation to prevent accessing outer scope receivers in nested blocks
- **`require()`** for argument validation in builders (vs `check()` for state validation)
- **Higher-order functions** — functions taking/returning functions
- **Coroutines:**
  - `runBlocking` — bridging regular code to coroutine world
  - `withTimeoutOrNull` — racing a timer against user input
  - `withContext(Dispatchers.IO)` — moving blocking I/O to a background thread
  - Understanding that `readln()` blocks a thread and coroutines are cooperative
- **TDD (Test-Driven Development):**
  - Red/green/refactor cycle
  - `assertFailsWith<IllegalArgumentException>` for validation tests
  - Isolating the thing under test (filling all valid fields to test one invalid field)
  - Testing pure logic (scoring, pass/fail) separately from I/O (prompting, printing)

### Architecture
```
DSL Layer:     quiz() → QuizBuilder → QuestionBuilder → Quiz (immutable)
Runner Layer:  QuizRunner (thin I/O shell)
Logic Layer:   AnswerRecord (correctness), QuizResult (scoring)
```

---

## What Was Intentionally Skipped

| Feature | Reason |
|---|---|
| Statistics in Project 2 & 3 | Already implemented thoroughly in Project 1. No new Kotlin concepts. |
| Note content/markdown field | The CLI doesn't render markdown. "Markdown" was thematic, not a requirement. No Kotlin learning value. |
| System editor integration | Opens notepad/vim for editing. Teaches process management, not Kotlin. |
| `.kts` script loading | Requires Kotlin Scripting engine with heavy configuration (multiple dependencies, script definitions, classpath management). More infrastructure fighting than Kotlin learning. |
| JSON/kotlinx.serialization | Already familiar from Android. Standard `@Serializable` usage wouldn't teach anything new. Polymorphic serialization with sealed classes would be interesting but required a sealed `QuestionType` refactor that wasn't worth doing just for this. |
| Score persistence | Already did file persistence in Project 1. Same patterns. |
| Categories/tags on quizzes | Already implemented tags in Project 2. Nothing new. |
| Sealed class QuestionType refactor | Understood the concept from Project 2. The refactor would touch many files for minimal new learning. Recognized it's "just typing it out" at this point. |

---

## Progression Summary

| Project | Core Kotlin Skill | Difficulty |
|---|---|---|
| 1 - Expense Tracker | Collections, extensions, data classes, enums | Beginner → Intermediate |
| 2 - Note CLI | Sealed classes, command pattern, higher-order functions | Intermediate |
| 3 - Quiz DSL | Lambdas with receivers, DSL builders, coroutines, TDD | Intermediate → Advanced |

### The Key Mental Shifts
1. **Stop writing for-loops** → Use `map`, `filter`, `groupBy`, `sumOf`
2. **Stop writing utility classes** → Use extension functions
3. **Stop writing `switch` + `instanceof`** → Use sealed classes + exhaustive `when`
4. **Stop writing builder boilerplate** → Use lambdas with receivers
5. **Stop using `Scanner`** → Use `readln()` + `toIntOrNull()`
6. **Stop writing POJOs** → Use `data class` with `copy()`
7. **Stop writing singletons manually** → Use `object`
