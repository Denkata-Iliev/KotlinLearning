object CommandExecutor {
    private val repo = NoteRepo
    fun execute(command: Command): CommandResult = when (command) {
        is Command.Add -> add(command)
        is Command.Archive -> TODO()
        is Command.Details -> show(command)
        is Command.Edit -> TODO()
        is Command.EditTags -> TODO()
        is Command.ListAll -> listAll(command)
        is Command.Delete -> delete(command)
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

    private fun delete(command: Command.Delete): CommandResult {
        return when (repo.deleteById(command.id)) {
            true -> CommandResult.Success("Note deleted successfully.")
            false -> CommandResult.Error("Note with this id not found.")
        }
    }

    private fun show(command: Command.Details): CommandResult {
        val note = repo.getById(command.id) ?: return CommandResult.Error("Note with this id not found.")
        println("""
            Title: ${note.title}
            Tags: ${note.tags.joinToString(", ")}
            Priority: ${note.priority}
        """.trimIndent())
        return CommandResult.Success("")
    }

    private fun add(command: Command.Add): CommandResult {
        repo.save(
            Note(
                title = command.title,
                tags = command.tags,
                priority = command.priority,
            )
        )
        return CommandResult.Success("Note added successfully.")
    }

    private fun listAll(command: Command.ListAll): CommandResult {
        val headers = listOf("Id", "Title", "Tags", "Priority")
        val notes = repo.getAll().filter { note ->
            (command.tags.isEmpty() || note.tags.any(command.tags::contains))
                    && (command.priority == 0 || note.priority == command.priority)
        }

        val rows = notes.map {
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