object NoteRepo {
    private val notes: MutableList<Note> = mutableListOf()

    fun save(note: Note) {
        notes.add(note.copy(id = notes.size + 1))
    }

    fun getAll(): List<Note> = notes.toList()

    fun getById(id: Int): Note? = notes.find { it.id == id }

    fun delete(id: Int) = notes.removeIf { it.id == id }

    fun archive(id: Int) = updateNote(id) { it.copy(archived = true) }

    fun unarchive(id: Int) = updateNote(id) { it.copy(archived = false) }

    fun edit(id: Int, title: String) = updateNote(id) { it.copy(title = title) }

    private fun updateNote(id: Int, transformNote: (note: Note) -> Note): Boolean {
        val index = notes.indexOfFirst { it.id == id }
            .takeIf { it != -1 } ?: return false

        notes[index] = transformNote(notes[index])
        return true
    }
}