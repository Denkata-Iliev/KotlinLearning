package org.example

import org.example.data.Question
import org.example.data.Quiz

class QuizRunner {
}

data class AnswerRecord(
    val question: Question,
    val selected: List<Int>
) {
    val correct: Boolean
        get() {
            val correctIndexes = question.options.mapIndexedNotNull { index, option ->
                if (option.correct) index else null
            }
            return correctIndexes.sorted() == selected.sorted()
        }
}

data class QuizResult(
    val quiz: Quiz,
    val answers: List<AnswerRecord>,
) {
    val score: Int get() = answers.filter { it.correct }.sumOf { it.question.points }

    val percentage: Int get() = if (quiz.totalPoints == 0) 0 else (score * 100) / quiz.totalPoints

    val passed: Boolean get() = percentage >= quiz.passingScorePercent
}