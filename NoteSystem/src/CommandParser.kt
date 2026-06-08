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

        val result = ParseResult.Success(Command.Delete(id))
        if (split.size == 2) {
            println("Are you sure you want to delete this note? y/n")

            val confirmed = when (readln()) {
                "y" -> true
                "n" -> false
                else -> return ParseResult.Error("Answer not recognized. Operation cancelled.")
            }

            if (confirmed) {
                return result
            }
        }

        if (split[2] != "--force") {
            return ParseResult.Error("Usage: delete <id> [--force]. Id must be a number.")
        }

        return result
    }

    private fun parseShow(trimmedInput: String): ParseResult {
        val split = trimmedInput.split(" ")
        if (split.size != 2) {
            ParseResult.Error("Usage: show <id>. Id must be a number.")
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