sealed class Command {
    data class Add(val title: String, val tags: List<String> = emptyList(), val priority: Int = 0) : Command()
    data class Delete(val id: Int, val force: Boolean = false) : Command()
    data class ListAll(
        val tags: List<String> = emptyList(),
        val priority: Int = 0,
        val archived: Boolean = false,
        val all: Boolean = false
    ) : Command()

    data class Details(val id: Int) : Command()
    data class Edit(val id: Int, val title: String) : Command()
    data class Search(val query: String) : Command()
    data class EditTags(val id: Int, val tags: List<String>) : Command()
    data class Archive(val id: Int) : Command()
    data class Unarchive(val id: Int) : Command()

    data object DisplayStats : Command()
    data object Help : Command()
    data object Exit : Command()
    data object Unknown : Command()
}