import org.example.dsl.question
import kotlin.test.Test
import kotlin.test.assertEquals

class QuestionBuilderTests {
    @Test
    fun `question has correct title`() {
        val q = question("What is Int") {}

        assertEquals("What is Int", q.question)
    }

    @Test
    fun `question has correct explanation`() {
        val q = question("What is Int") {
            explanation = "some explanation"
        }

        assertEquals("some explanation", q.explanation)
    }

    @Test
    fun `question has correct points assigned`() {
        val q = question("What is Int") {
            points = 2
        }

        assertEquals(2, q.points)
    }

    @Test
    fun `question has 1 option with correct title`() {
        val q = question("What is Int") {
            option("idk")
        }

        assertEquals(1, q.options.size)
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
}