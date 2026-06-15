private const val HELP_MENU = """
📝 Note CLI - Help
══════════════════════════════════════════════════════════

Available commands:

  add "title" [--tags=t1,t2] [--priority=N]
      Create a new note with optional tags and priority.
      Example: add "Buy groceries" --tags=shopping,personal --priority=3

  list [--tags=t1,t2] [--priority=N] [--archived] [--all]
      List notes. Optionally filter by tags, priority, or status.
      Example: list --tags=work --priority=1
      Example: list --archived
      Example: list --all

  show <id>
      Display full details of a note.
      Example: show 5

  edit <id> "new title"
      Update the title of an existing note.
      Example: edit 5 "Updated title"

  tag <id> t1,t2,...
      Replace tags on a note.
      ⚠️  This replaces ALL existing tags. It does not append.
      Example: tag 5 work,urgent

  delete <id> [--force]
      Delete a note. Asks for confirmation unless --force is used.
      Example: delete 5
      Example: delete 5 --force

  search "query"
      Search notes by title (case-insensitive).
      Example: search "groceries"

  archive <id>
      Archive a note. Archived notes are hidden from default list.
      Example: archive 5

  unarchive <id>
      Restore an archived note.
      Example: unarchive 5

  help
      Show this help message.

  exit
      Exit the application.

══════════════════════════════════════════════════════════
"""

private const val NOTE_ID_NOT_FOUND = "Note with this id not found."

object CommandExecutor {
    private val repo = NoteRepo
    fun execute(command: Command): CommandResult = when (command) {
        is Command.Add -> add(command)
        is Command.Archive -> archive(command)
        is Command.Unarchive -> unarchive(command)
        is Command.Details -> show(command)
        is Command.Edit -> edit(command)
        is Command.EditTags -> editTags(command)
        is Command.ListAll -> listAll(command)
        is Command.Delete -> delete(command)
        is Command.Search -> TODO()
        Command.Help -> CommandResult.Success(HELP_MENU)
        Command.Exit -> {
            println("Byee 👋🏻!")
            CommandResult.Exit
        }

        Command.Unknown -> CommandResult.Error("Unknown command. Try again.")
    }

    private fun editTags(command: Command.EditTags) =
        executeAndMapResult("Note tags updated successfully") {
            repo.editTags(command.id, command.tags)
        }

    private fun edit(command: Command.Edit) =
        executeAndMapResult("Note updated successfully.") {
            repo.edit(command.id, command.title)
        }

    private fun archive(command: Command.Archive) =
        executeAndMapResult("Note archived successfully.") {
            repo.archive(command.id)
        }

    private fun unarchive(command: Command.Unarchive) =
        executeAndMapResult("Note unarchived successfully.") {
            repo.unarchive(command.id)
        }

    private fun delete(command: Command.Delete): CommandResult {
        if (!command.force) {
            println("Are you sure you want to delete this note? y/n")
            when (readln().lowercase().trim()) {
                "y" -> { /* Falls through to the delete block below. */ }

                "n" -> return CommandResult.Success("Cancelled.")

                else -> return CommandResult.Error("Answer not recognized.")
            }
        }

        return executeAndMapResult("Note deleted successfully.") {
            repo.delete(command.id)
        }
    }

    private fun show(command: Command.Details): CommandResult {
        val note = repo.getById(command.id) ?: return CommandResult.Error(NOTE_ID_NOT_FOUND)

        return CommandResult.Success(buildString {
            appendLine("Title: ${note.title}")
            appendLine("Tags: ${note.tags.joinToString(", ")}")
            appendLine("Priority: ${note.priority}")
        })
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
        val headers = buildList {
            add("Id")
            add("Title")
            add("Tags")
            add("Priority")
            if (command.all) {
                add("Archived")
            }
        }

        val notes = repo.getAll().filter { note ->
            (command.tags.isEmpty() || note.tags.any(command.tags::contains))
                    && (command.priority == 0 || note.priority == command.priority)
                    && (command.archived == note.archived || command.all)
        }

        val rows = notes.map {
            buildList {
                add(it.id.toString())
                add(it.title)
                add(it.tags.joinToString(", "))
                add(it.priority.toString())
                if (command.all) {
                    val v = if (it.archived) "Yes" else "No"
                    add(v)
                }
            }
        }

        return CommandResult.Success(TableRenderer.renderTable(headers, rows))
    }

    private fun executeAndMapResult(successMessage: String, action: () -> Boolean): CommandResult {
        return when (action()) {
            true -> CommandResult.Success(successMessage)
            false -> CommandResult.Error(NOTE_ID_NOT_FOUND)
        }
    }
}