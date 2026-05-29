fun main() {
    generateSequence {
        print("notes> ")
        readln()
    }
        .map(CommandParser::parseCommand)
        .takeWhile { it != Command.Exit }
        .map(CommandExecutor::execute)
        .forEach(::println)
}