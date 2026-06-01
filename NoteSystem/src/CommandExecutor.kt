object CommandExecutor {
    private val repo = NoteRepo
    fun execute(command: Command): CommandResult = when (command) {
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
        Command.Exit -> {
            println("Byee 👋🏻!")
            CommandResult.Exit
        }

        Command.Unknown -> CommandResult.Error("Unknown command. Try again.")
    }

    private fun executeAdd(command: Command.Add): CommandResult {
        repo.save(
            Note(
                title = command.title,
                tags = command.tags,
                priority = command.priority,
            )
        )
        return CommandResult.Success("Note added successfully.")
    }

    private fun executeListAll(): CommandResult {
        val headers = listOf("Id", "Title", "Tags", "Priority")
        val allNotes = repo.getAll()
        val rows = allNotes.map {
            listOf(
                it.id.toString(),
                it.title,
                it.tags.joinToString(", "),
                it.priority.toString()
            )
        }

        return CommandResult.Success(TableRenderer.renderTable(headers, rows))
    }
}