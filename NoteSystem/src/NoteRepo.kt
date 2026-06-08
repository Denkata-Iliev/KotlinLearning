object NoteRepo {
    private val notes: MutableList<Note> = mutableListOf()

    fun save(note: Note) {
        notes.add(note.copy(id = notes.size + 1))
    }

    fun getAll(): List<Note> = notes.toList()

    fun getById(id: Int): Note? = notes.find { it.id == id }

    fun deleteById(id: Int): Boolean {
        return notes.removeIf { it.id == id }
    }
}