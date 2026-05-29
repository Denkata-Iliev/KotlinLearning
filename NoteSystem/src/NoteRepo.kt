object NoteRepo {
    private val notes: MutableList<Note> = mutableListOf()

    fun save(note: Note) {
        notes.add(note.copy(id = notes.size + 1))
    }

    fun getAll(): List<Note> = notes.toList()
}