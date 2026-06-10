data class Note(
    val id: Int = 0,
    val title: String,
    val tags: List<String>,
    val priority: Int,
    val archived: Boolean = false,
)
