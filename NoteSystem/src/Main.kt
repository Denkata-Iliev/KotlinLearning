fun main() {
    generateSequence {
        print("notes> ")
        readln()
    }
        .map(CommandParser::parseCommand)
        .map(CommandExecutor::execute)
        .takeWhile { it != CommandResult.Exit }
        .forEach(CommandResult::display)
}