package org.example.dsl

import org.example.data.Quiz

class QuizBuilder(private val name: String) {
    var description: String = ""
    var passingScorePercent = 50

    fun build() = Quiz(
        title = name,
        description = description,
        passingScorePercent = passingScorePercent,
        questions = emptyList()
    )
}

fun quiz(name: String, init: QuizBuilder.() -> Unit): Quiz {
    val quiz = QuizBuilder(name)
    quiz.init()
    return quiz.build()
}