package org.example.dsl

import org.example.data.Quiz

class QuizBuilder(private val name: String) {
    var description = ""
    var passingScorePercent = 50

    fun build(): Quiz {
        require(name.isNotBlank()) { "name must not be blank" }
        require(description.isNotBlank()) { "description must not be blank" }
        require(passingScorePercent > 0) { "passingScorePercent must be greater than zero" }

        return Quiz(
            title = name,
            description = description,
            passingScorePercent = passingScorePercent,
            questions = emptyList()
        )
    }
}

fun quiz(name: String, init: QuizBuilder.() -> Unit): Quiz {
    val quiz = QuizBuilder(name)
    quiz.init()
    return quiz.build()
}