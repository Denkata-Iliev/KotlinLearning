object CommandParser {
    private val addPattern = Regex("""add\s+"([^"]+)"(.*)""")
    private val listAllPattern = Regex("""list\s*(.*)""")
    private val flagPattern = Regex("""--(\w+)(?:=(\S+))?""")

    fun parseCommand(input: String): Command {
        val trimmedInput = input.trim()
        return when {
            trimmedInput.startsWith("add ") -> parseAdd(trimmedInput)
            trimmedInput.startsWith("list") -> parseListAll(trimmedInput)
            trimmedInput == "exit" -> Command.Exit
            else -> Command.Unknown
        }
    }

    private fun parseAdd(trimmedInput: String): Command {
        val match = addPattern.find(trimmedInput) ?: return Command.Unknown
        val title = match.groupValues[1]

        val parsedFlags = parseFlags(match.groupValues[2])
        val tags = parsedFlags["tags"]?.split(",") ?: emptyList()
        val priority = parsedFlags["priority"]?.toIntOrNull() ?: 0

        return Command.Add(title, tags, priority)
    }

    private fun parseListAll(trimmedInput: String): Command {
        val match = listAllPattern.find(trimmedInput) ?: return Command.Unknown

        val parsedFlags = parseFlags(match.groupValues[1])
        val tags = parsedFlags["tags"]?.split(",") ?: emptyList()
        val priority = parsedFlags["priority"]?.toIntOrNull() ?: 0

        return Command.ListAll(tags, priority)
    }

    private fun parseFlags(input: String): Map<String, String> =
        flagPattern.findAll(input)
            .associate { it.groupValues[1] to (it.groupValues[2].ifEmpty { "true" }) }
}