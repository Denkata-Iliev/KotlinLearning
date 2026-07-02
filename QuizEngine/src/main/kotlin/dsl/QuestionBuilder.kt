package org.example.dsl

import org.example.data.Option
import org.example.data.Question

@QuizDslMarker
class QuestionBuilder(private val question: String) {
    private val options = mutableListOf<Option>()
    var explanation = ""
    var points = 0
    var timerSeconds: Int? = null

    fun build(): Question {
        require(question.isNotBlank()) { "question must not be blank" }
        require(explanation.isNotBlank()) { "explanation must not be blank" }
        require(points > 0) { "points must be greater than 0" }
        require(options.size > 1) { "question must have more than one options" }
        require(options.any { it.correct }) { "question must have at least 1 correct option" }
        require(!options.all { it.correct }) { "question can't have all correct options" }

        return Question(
            question = question,
            explanation = explanation,
            options = options,
            points = points,
            timerSeconds = timerSeconds
        )
    }

    fun option(answer: String, correct: Boolean = false) {
        options.add(Option(answer, correct))
    }
}