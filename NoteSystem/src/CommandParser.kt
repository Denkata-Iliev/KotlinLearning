private const val TAGS_PRIO_EXPLANATION = "Tags must be separated by a single comma. Priority must be a number."
private const val TAGS_PRIO_EXAMPLE = "[--tags=t1,t2] [--priority=10]"
private const val ID_MUST_BE_NUMBER = "Id must be a number"
private const val EDIT_USAGE_MESSAGE =
    """Usage: edit <id> "<title>". $ID_MUST_BE_NUMBER. Title must be surrounded by quotation marks."""
private const val SPACE_SPLIT = " "
private const val COMMA_SPLIT = ","

object CommandParser {
    private val addPattern = Regex("""add\s+"([^"]+)"(.*)""")
    private val listAllPattern = Regex("""list\s*(.*)""")
    private val flagPattern = Regex("""--(\w+)(?:=(\S+))?""")
    private val editPattern = Regex("""edit\s[1-9]\s"([^"]+)"(.*)""")

    fun parseCommand(input: String): ParseResult {
        val trimmedInput = input.trim()
        return when {
            trimmedInput.startsWith("add ") -> parseAdd(trimmedInput)
            trimmedInput.startsWith("list") -> parseListAll(trimmedInput)
            trimmedInput.startsWith("show ") -> parseShow(trimmedInput)
            trimmedInput.startsWith("delete ") -> parseDelete(trimmedInput)
            trimmedInput.startsWith("archive ") -> parseArchive(trimmedInput)
            trimmedInput.startsWith("unarchive ") -> parseUnarchive(trimmedInput)
            trimmedInput.startsWith("edit ") -> parseEdit(trimmedInput)
            trimmedInput == "exit" -> ParseResult.Success(Command.Exit)
            else -> ParseResult.Success(Command.Unknown)
        }
    }

    private fun parseEdit(trimmedInput: String): ParseResult {
        val split = trimmedInput.split(SPACE_SPLIT)
        if (split.size < 3) {
            return ParseResult.Error(EDIT_USAGE_MESSAGE)
        }
        if (trimmedInput.count { it == '"' } < 2) {
            return ParseResult.Error(EDIT_USAGE_MESSAGE)
        }

        val match = editPattern.find(trimmedInput) ?: return ParseResult.Error(EDIT_USAGE_MESSAGE)
        val id = split[1].toIntOrNull() ?: return ParseResult.Error("$ID_MUST_BE_NUMBER.")

        val title = match.groupValues[1]

        return ParseResult.Success(Command.Edit(id, title))
    }

    private fun parseArchive(trimmedInput: String): ParseResult {
        val split = trimmedInput.split(SPACE_SPLIT)

        val id = split[1].toIntOrNull() ?: return ParseResult.Error("Usage: archive <id>. $ID_MUST_BE_NUMBER.")

        return ParseResult.Success(Command.Archive(id))
    }

    private fun parseUnarchive(trimmedInput: String): ParseResult {
        val split = trimmedInput.split(SPACE_SPLIT)

        val id = split[1].toIntOrNull() ?: return ParseResult.Error("Usage: unarchive <id>. $ID_MUST_BE_NUMBER.")

        return ParseResult.Success(Command.Unarchive(id))
    }

    private fun parseDelete(trimmedInput: String): ParseResult {
        val split = trimmedInput.lowercase().split(SPACE_SPLIT)
        if (split.size !in 2..3) {
            return ParseResult.Error("Usage: delete <id> [--force]. $ID_MUST_BE_NUMBER.")
        }

        val id = split[1].toIntOrNull() ?: return ParseResult.Error("Usage: delete <id> [--force]. $ID_MUST_BE_NUMBER.")

        // just a note for me:
        // `val force = split.getOrNull(2) == "--force"`
        // could also be used, and it skips the 2 `if` checks I have.
        // Mine tells you if you misspelled something in the flag, the other just skips it.
        // Doesn't really matter
        if (split.size == 3 && split[2] != "--force") {
            return ParseResult.Error("Usage: delete <id> [--force]. $ID_MUST_BE_NUMBER.")
        }

        if (split.size == 3) {
            return ParseResult.Success(Command.Delete(id, force = true))
        }

        return ParseResult.Success(Command.Delete(id))
    }

    private fun parseShow(trimmedInput: String): ParseResult {
        val split = trimmedInput.split(SPACE_SPLIT)
        if (split.size != 2) {
            return ParseResult.Error("Usage: show <id>. $ID_MUST_BE_NUMBER.")
        }

        val id = split[1].toIntOrNull() ?: return ParseResult.Error("Usage: show <id>. $ID_MUST_BE_NUMBER.")
        return ParseResult.Success(Command.Details(id))
    }

    private fun parseAdd(trimmedInput: String): ParseResult {
        val match = addPattern.find(trimmedInput)
            ?: return ParseResult.Error("""Usage: add "<title>" $TAGS_PRIO_EXAMPLE. $TAGS_PRIO_EXPLANATION""")

        val title = match.groupValues[1]
        val parsedFlags = parseFlags(match.groupValues[2])
        val tags = parsedFlags["tags"]?.split(COMMA_SPLIT) ?: emptyList()
        val priority = parsedFlags["priority"]?.toIntOrNull() ?: 0

        return ParseResult.Success(Command.Add(title, tags, priority))
    }

    private fun parseListAll(trimmedInput: String): ParseResult {
        val match = listAllPattern.find(trimmedInput)
            ?: return ParseResult.Error("Usage: list $TAGS_PRIO_EXAMPLE [--archived] [--all]. $TAGS_PRIO_EXPLANATION")

        val parsedFlags = parseFlags(match.groupValues[1])
        val tags = parsedFlags["tags"]?.split(COMMA_SPLIT) ?: emptyList()
        val priority = parsedFlags["priority"]?.toIntOrNull() ?: 0

        val archived = parsedFlags["archived"]?.toBoolean() ?: false
        val all = parsedFlags["all"]?.toBoolean() ?: false

        if (archived && all) {
            return ParseResult.Error("You can't combine those flags.")
        }

        return ParseResult.Success(Command.ListAll(tags, priority, archived, all))
    }

    private fun parseFlags(input: String): Map<String, String> =
        flagPattern.findAll(input)
            .associate { it.groupValues[1] to (it.groupValues[2].ifEmpty { "true" }) }
}