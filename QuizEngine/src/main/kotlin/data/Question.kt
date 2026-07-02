package org.example.data

data class Question(
    val question: String,
    val explanation: String,
    val options: List<Option>,
    val points: Int = 1,
) {
    val isMultipleChoice: Boolean get() = options.count { it.correct } > 1
}
