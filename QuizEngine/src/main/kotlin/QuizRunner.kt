package org.example

import org.example.data.Question
import org.example.data.Quiz

class QuizRunner(private val quiz: Quiz) {
    fun run() {
        printHeader()

        val answers = quiz.questions.mapIndexed { index, question ->
            printQuestion(index, question, quiz.questions.size)
            val selectedOptions = promptForAnswer(question)
            val record = AnswerRecord(question, selectedOptions)
            printFeedback(record)
            record
        }

        val result = QuizResult(quiz, answers)
        printResult(result)
    }

    private fun printQuestion(index: Int, question: Question, totalQuestions: Int) {
        println("Question #${index + 1}/$totalQuestions (${question.points} pts)")
        println(question.question)
        question.options.forEachIndexed { index, option ->
            println("${index + 1}. ${option.answer}")
        }
    }

    private fun promptForAnswer(question: Question): List<Int> {
        val maxOption = question.options.size

        while (true) {
            if (question.isMultipleChoice) {
                print("Your answers (e.g. 1,3): ")
            } else {
                print("Your answer (1-$maxOption): ")
            }

            val parsed = readln()
                .split(",")
                .mapNotNull { it.trim().toIntOrNull() }
                .filter { it in 1..maxOption }
                .map { it - 1 } // convert to 0-based indices

            if (parsed.isEmpty()) {
                println("Invalid input, try again.")
                continue
            }

            if (!question.isMultipleChoice && parsed.size > 1) {
                println("This question only accepts one answer.")
                continue
            }

            return parsed
        }
    }

    private fun printFeedback(record: AnswerRecord) {
        if (record.correct) {
            println("Correct!")
        } else {
            val correctAnswers = record.question.options.mapIndexedNotNull { index, option ->
                (index + 1).takeIf { option.correct }
            }
                .joinToString(", ")
            println("Wrong! Correct answer(s): $correctAnswers")
        }
        println(record.question.explanation)
    }

    private fun printResult(result: QuizResult) {
        println("\n" + "=".repeat(40))
        println("Score: ${result.score}/${result.quiz.totalPoints} (${result.percentage}%)")
        println(if (result.passed) "PASSED!" else "Not passed")
    }

    private fun printHeader() {
        println("=".repeat(40))
        println("   ${quiz.title}")
        println("   ${quiz.questions.size} questions | Pass: ${quiz.passingScorePercent}%")
        println("=".repeat(40))
    }
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