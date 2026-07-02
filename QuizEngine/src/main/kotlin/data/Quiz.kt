package org.example.data

data class Quiz(
    val title: String,
    val description: String,
    val passingScorePercent: Int,
    val questions: List<Question>,
) {
    val totalPoints: Int get() = questions.sumOf { it.points }
}
