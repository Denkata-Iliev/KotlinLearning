import org.example.dsl.question
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class QuestionBuilderTests {
    @Test
    fun `question has correct title`() {
        val q = question("What is Int") {
            explanation = "Int is a type for whole numbers"
            points = 2
            option("I don't know")
            option("A type", correct = true)
        }

        assertEquals("What is Int", q.question)
    }

    @Test
    fun `question has correct explanation`() {
        val q = question("What is Int?") {
            explanation = "some explanation"
            points = 2
            option("I don't know")
            option("A type", correct = true)
        }

        assertEquals("some explanation", q.explanation)
    }

    @Test
    fun `question has correct points assigned`() {
        val q = question("What is Int?") {
            explanation = "Int is a type for whole numbers"
            points = 2
            option("I don't know")
            option("A type", correct = true)
        }

        assertEquals(2, q.points)
    }

    @Test
    fun `question has 1 option with correct title`() {
        val q = question("What is Int?") {
            explanation = "Int is a type for whole numbers"
            points = 2
            option("idk", correct = true)
            option("idk")
        }

        assertEquals(2, q.options.size)
        assertEquals("idk", q.options[0].answer)
    }

    @Test
    fun `question has correctly assigned title, explanation, points and options`() {
        val q = question("What is Int?") {
            explanation = "Int is a type for whole numbers"
            points = 2
            option("I don't know")
            option("A type", correct = true)
        }

        assertEquals("What is Int?", q.question)
        assertEquals("Int is a type for whole numbers", q.explanation)
        assertEquals(2, q.points)
        assertEquals("I don't know", q.options[0].answer)
        assertEquals("A type", q.options[1].answer)
        assertEquals(true, q.options[1].correct)
    }

    @Test
    fun `question with empty title throws`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            question("") {
                explanation = "Int is a type for whole numbers"
                points = 2
                option("I don't know")
                option("A type", correct = true)
            }
        }

        assertEquals("question must not be blank", ex.message)
    }

    @Test
    fun `question with empty explanation throws`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            question("What is Int?") {
                explanation = ""
                points = 2
                option("I don't know")
                option("A type", correct = true)
            }
        }

        assertEquals("explanation must not be blank", ex.message)
    }

    @Test
    fun `question with 0 or negative points throws`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            question("What is Int?") {
                explanation = "asd"
                points = 0
                option("I don't know")
                option("A type", correct = true)
            }
        }

        val ex2 = assertFailsWith<IllegalArgumentException> {
            question("What is Int?") {
                explanation = "asd"
                points = -1
                option("I don't know")
                option("A type", correct = true)
            }
        }

        assertEquals("points must be greater than 0", ex.message)
        assertEquals("points must be greater than 0", ex2.message)
    }

    @Test
    fun `question with no options throws`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            question("What is Int?") {
                explanation = "asd"
                points = 2
            }
        }

        assertEquals("question must have more than one options", ex.message)
    }

    @Test
    fun `question with no correct option throws`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            question("What is Int?") {
                explanation = "asd"
                points = 2
                option("I don't know")
                option("A type")
            }
        }

        assertEquals("question must have at least 1 correct option", ex.message)
    }

    @Test
    fun `question with all correct options throws`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            question("What is Int?") {
                explanation = "asd"
                points = 2
                option("I don't know", correct = true)
                option("A type", correct = true)
            }
        }

        assertEquals("question can't have all correct options", ex.message)
    }
}