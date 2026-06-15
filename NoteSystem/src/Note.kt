data class Note(
    val id: Int = 0,
    val title: String,
    val tags: List<String>,
    val priority: Int,
    val archived: Boolean = false,
)

fun Note.toRow(includeArchived: Boolean = false): List<String> =
    buildList {
        add(this@toRow.id.toString())
        add(this@toRow.title)
        add(this@toRow.tags.joinToString(", "))
        add(this@toRow.priority.toString())
        if (includeArchived) add(if (this@toRow.archived) "Yes" else "No")
    }