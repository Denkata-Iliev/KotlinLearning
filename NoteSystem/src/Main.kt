fun main() {
    generateSequence {
        print("notes> ")
        readln()
    }
        .map(CommandParser::parseCommand)
        .map {
            when (it) {
                is ParseResult.Success -> CommandExecutor.execute(it.command)
                is ParseResult.Error -> CommandResult.Error(it.error)
            }
        }
        .takeWhile { it != CommandResult.Exit }
        .forEach(CommandResult::display)
}