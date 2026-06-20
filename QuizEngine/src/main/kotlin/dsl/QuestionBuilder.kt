package org.example.dsl

import org.example.data.Option
import org.example.data.Question

class QuestionBuilder(private val question: String) {
    private val options = mutableListOf<Option>()
    var explanation = ""
    var points = 0

    fun build() = Question(
        question = question,
        explanation = explanation,
        options = options,
        points = points
    )

    fun option(answer: String, correct: Boolean = false) {
        options.add(Option(answer, correct))
    }
}

fun question(question: String, init: QuestionBuilder.() -> Unit): Question {
    val questionBuilder = QuestionBuilder(question)
    questionBuilder.init()
    return questionBuilder.build()
}