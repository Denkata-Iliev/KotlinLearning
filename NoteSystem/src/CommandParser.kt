object CommandParser {
    private val addPattern = Regex("""add\s+"([^"]+)"(.*)""")
    private val listAllPattern = Regex("""list\s*(.*)""")
    private val flagPattern = Regex("""--(\w+)(?:=(\S+))?""")

    fun parseCommand(input: String): ParseResult {
        val trimmedInput = input.trim()
        return when {
            trimmedInput.startsWith("add ") -> parseAdd(trimmedInput)
            trimmedInput.startsWith("list") -> parseListAll(trimmedInput)
            trimmedInput.startsWith("show ") -> parseShow(trimmedInput)
            trimmedInput.startsWith("delete ") -> parseDelete(trimmedInput)
            trimmedInput == "exit" -> ParseResult.Success(Command.Exit)
            else -> ParseResult.Success(Command.Unknown)
        }
    }

    private fun parseDelete(trimmedInput: String): ParseResult {
        val split = trimmedInput.lowercase().split(" ")
        if (split.size !in 2..3) {
            return ParseResult.Error("Usage: delete <id> [--force]. Id must be a number.")
        }

        val id = split[1].toIntOrNull() ?: return ParseResult.Error("Usage: delete <id> [--force]. Id must be a number.")

        // just a note for me:
        // `val force = split.getOrNull(2) == "--force"`
        // could also be used, and it skips the 2 `if` checks I have.
        // Mine tells you if you misspelled something in the flag, the other just skips it.
        // Doesn't really matter
        if (split.size == 3 && split[2] != "--force") {
            return ParseResult.Error("Usage: delete <id> [--force]. Id must be a number.")
        }

        if (split.size == 3) {
            return ParseResult.Success(Command.Delete(id, force = true))
        }

        return ParseResult.Success(Command.Delete(id))
    }

    private fun parseShow(trimmedInput: String): ParseResult {
        val split = trimmedInput.split(" ")
        if (split.size != 2) {
            return ParseResult.Error("Usage: show <id>. Id must be a number.")
        }

        val id = split[1].toIntOrNull() ?: return ParseResult.Error("Usage: show <id>. Id must be a number.")
        return ParseResult.Success(Command.Details(id))
    }

    private fun parseAdd(trimmedInput: String): ParseResult {
        val match = addPattern.find(trimmedInput) ?: return ParseResult.Error("Unknown command. Try again.")
        val title = match.groupValues[1]

        val parsedFlags = parseFlags(match.groupValues[2])
        val tags = parsedFlags["tags"]?.split(",") ?: emptyList()
        val priority = parsedFlags["priority"]?.toIntOrNull() ?: 0

        return ParseResult.Success(Command.Add(title, tags, priority))
    }

    private fun parseListAll(trimmedInput: String): ParseResult {
        val match = listAllPattern.find(trimmedInput) ?: return ParseResult.Error("Unknown command. Try again.")

        val parsedFlags = parseFlags(match.groupValues[1])
        val tags = parsedFlags["tags"]?.split(",") ?: emptyList()
        val priority = parsedFlags["priority"]?.toIntOrNull() ?: 0

        return ParseResult.Success(Command.ListAll(tags, priority))
    }

    private fun parseFlags(input: String): Map<String, String> =
        flagPattern.findAll(input)
            .associate { it.groupValues[1] to (it.groupValues[2].ifEmpty { "true" }) }
}