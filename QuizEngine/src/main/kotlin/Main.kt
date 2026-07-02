package org.example

import org.example.dsl.quiz

fun main() {
    val kotlinQuiz = quiz("Kotlin Basics") {
        description = "Test your Kotlin fundamentals"
        passingScorePercent = 70

        question("What is the Kotlin equivalent of 'switch'?") {
            explanation = "'when' can be used as both statement and expression"
            points = 15
            timerSeconds = 5

            option("when", correct = true)
            option("match")
            option("case")
            option("select")
        }

        question("What does 'val' declare?") {
            explanation = "'val' declares an immutable reference"
            points = 10

            option("Mutable variable")
            option("Immutable reference", correct = true)
            option("Constant")
            option("Value type")
        }

        question("Which keyword declares a mutable variable?") {
            explanation = "'var' allows reassignment, while 'val' does not."
            points = 10

            option("var", correct = true)
            option("val")
            option("let")
            option("mutable")
        }

        question("What is the default visibility modifier in Kotlin?") {
            explanation = "Declarations are public by default."
            points = 15

            option("private")
            option("internal")
            option("public", correct = true)
            option("protected")
        }

        question("Which of the following are Kotlin scope functions?") {
            explanation = "Kotlin provides several scope functions, including let, run, apply, also, and with."
            points = 25

            option("let", correct = true)
            option("apply", correct = true)
            option("also", correct = true)
            option("foreach")
        }

        question("Which collection types in Kotlin are immutable by default?") {
            explanation = "listOf(), setOf(), and mapOf() return read-only collections by default."
            points = 25

            option("List", correct = true)
            option("MutableList")
            option("MutableSet")
            option("Map", correct = true)
        }

        question("Which function is used to safely execute code on a nullable object?") {
            explanation = "'let' is commonly used with the safe-call operator (?.)."
            points = 15

            option("apply")
            option("run")
            option("let", correct = true)
            option("also")
        }

        question("Which operator safely accesses a nullable value?") {
            explanation = "The safe-call operator returns null instead of throwing an exception."
            points = 20

            option("!!")
            option("?.", correct = true)
            option("?:")
            option("::")
        }

        question("What does the Elvis operator (?:) do?") {
            explanation = "It provides a default value when the expression on the left is null."
            points = 20

            option("Performs type casting")
            option("Throws an exception")
            option("Provides a fallback value", correct = true)
            option("Checks equality")
        }

        question("Which keyword is used for inheritance in Kotlin?") {
            explanation = "Classes are final by default and must be marked 'open' to be inherited."
            points = 20

            option("extends")
            option("inherits")
            option("open", correct = true)
            option("override")
        }

        question("What is the purpose of a data class?") {
            explanation = "Data classes automatically generate useful methods such as equals(), hashCode(), and toString()."
            points = 25

            option("To store only primitive values")
            option("To automatically generate common utility methods", correct = true)
            option("To create singleton objects")
            option("To improve runtime performance")
        }

        question("Which scope function returns the receiver object?") {
            explanation = "'apply' returns the receiver, making it convenient for object configuration."
            points = 20

            option("let")
            option("run")
            option("apply", correct = true)
            option("with")
        }

        question("What is the purpose of the 'object' keyword?") {
            explanation = "The 'object' keyword creates a singleton instance."
            points = 20

            option("Creates an abstract class")
            option("Creates a singleton", correct = true)
            option("Creates an interface")
            option("Creates an enum")
        }

        question("What does the 'is' operator check?") {
            explanation = "The 'is' operator performs a runtime type check."
            points = 15

            option("Object equality")
            option("Reference equality")
            option("Whether an object is of a given type", correct = true)
            option("Nullability")
        }
    }

    QuizRunner(kotlinQuiz).run()
}