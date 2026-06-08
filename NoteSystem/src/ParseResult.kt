sealed class ParseResult {
    data class Success(val command: Command) : ParseResult()
    data class Error(val error: String) : ParseResult()
}