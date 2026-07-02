package org.example.dsl

import org.example.data.Question
import org.example.data.Quiz

class QuizBuilder(private val name: String) {
    private val questions = mutableListOf<Question>()
    var description = ""
    var passingScorePercent = 0

    fun build(): Quiz {
        require(name.isNotBlank()) { "name must not be blank" }
        require(description.isNotBlank()) { "description must not be blank" }
        require(passingScorePercent > 0) { "passingScorePercent must be greater than zero" }
        require(questions.size >= 3) { "a quiz must contain at least 3 questions" }

        return Quiz(
            title = name,
            description = description,
            passingScorePercent = passingScorePercent,
            questions = questions
        )
    }

    fun question(question: String, init: QuestionBuilder.() -> Unit) {
        questions += QuestionBuilder(question).apply(init).build()
        // Only leaving this here as reference for alternative way
        // val builder = QuestionBuilder(question)
        // builder.init()
        // questions.add(builder.build())
    }
}

fun quiz(name: String, init: QuizBuilder.() -> Unit) = QuizBuilder(name).apply(init).build()
// This is equivalent to the three lines in the question func or the test helper in the QuestionBuilderTests class