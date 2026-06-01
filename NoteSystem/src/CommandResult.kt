sealed class CommandResult {
    data class Success(val message: String) : CommandResult()
    data class Error(val message: String) : CommandResult()
    data object Exit : CommandResult()

    fun display() {
        when (this) {
            is Success -> println(message)
            is Error -> println(message)
            is Exit -> {}
        }
    }
}
