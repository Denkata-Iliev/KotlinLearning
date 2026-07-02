import org.example.dsl.quiz
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class QuizBuilderTests {
    @Test
    fun `quiz has correct name`() {
        val q = quiz("some name") {
            description = "my description"
            passingScorePercent = 50
        }
        assertEquals("some name", q.title)
    }

    @Test
    fun `quiz has correct description`() {
        val q = quiz("some name") {
            description = "some description"
            passingScorePercent = 50
        }

        assertEquals("some description", q.description)
    }

    @Test
    fun `quiz has correct passingScorePercent`() {
        val q = quiz("some name") {
            description = "some description"
            passingScorePercent = 60
        }

        assertEquals(60, q.passingScorePercent)
    }

    // Could also verify err message, but this is enough now
    @Test
    fun `empty quiz title throws`() {
        assertFailsWith<IllegalArgumentException> {
            quiz("") {}
        }
    }

    @Test
    fun `empty quiz description throws`() {
        assertFailsWith<IllegalArgumentException> {
            quiz("some name") {}
        }
    }

    @Test
    fun `passing score 0 throws`() {
        assertFailsWith<IllegalArgumentException> {
            quiz("some name") {
                description = "some description"
                passingScorePercent = 0
            }
        }
    }

    @Test
    fun `negative passing score throws`() {
        assertFailsWith<IllegalArgumentException> {
            quiz("some name") {
                description = "some description"
                passingScorePercent = -1
            }
        }
    }

    // Building a quiz with questions
    @Test
    fun `quiz has valid props and valid questions`() {
        val q = quiz("some name") {
            description = "my description"
            passingScorePercent = 50

            question("what is this?") {
                points = 2

                option("answer 1", correct = true)
                option("answer 2")
                option("answer 3")

                explanation = "i dont know"
            }
        }

        assertEquals(1, q.questions.size)
        assertEquals(2, q.questions[0].points)
        assertEquals("i dont know", q.questions[0].explanation)
        assertEquals(true, q.questions[0].options[0].correct)
        assertEquals(false, q.questions[0].options[1].correct)
        assertEquals(false, q.questions[0].options[2].correct)
    }

    @Test
    fun `quiz with no questions throws`() {
        assertFailsWith<IllegalArgumentException> {
            quiz("some name") {
                description = "my description"
                passingScorePercent = 50
            }
        }
    }
}
