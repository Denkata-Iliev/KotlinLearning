object CommandExecutor {
    private val repo = NoteRepo
    fun execute(command: Command): String = when (command) {
        is Command.Add -> executeAdd(command)
        is Command.Archive -> TODO()
        is Command.Details -> TODO()
        is Command.Edit -> TODO()
        is Command.EditTags -> TODO()
        is Command.ListAll -> executeListAll()
        is Command.Remove -> TODO()
        is Command.Search -> TODO()
        is Command.Unarchive -> TODO()
        Command.Help -> TODO()
        Command.DisplayStats -> TODO()
        Command.Exit -> "Byee!"
        Command.Unknown -> "Unknown command. Try again."
    }

    private fun executeAdd(command: Command.Add): String {
        repo.save(Note(
            title = command.title,
            tags = command.tags,
            priority = command.priority,
        ))
        return "Note added successfully."
    }

    private fun executeListAll() = repo.getAll().joinToString("\n") {
        """
            Note #${it.id} - ${it.title}
            Tags: ${it.tags.joinToString(", ")}
            Priority: ${it.priority}
        """.trimIndent()
    }
}