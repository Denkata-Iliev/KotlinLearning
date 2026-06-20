import org.example.dsl.quiz
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class QuizBuilderTests {
    @Test
    fun `quiz has correct name`() {
        val q = quiz("some name") {
            description = "my description"
        }
        assertEquals("some name", q.title)
    }

    @Test
    fun `quiz has correct description`() {
        val q = quiz("some name") {
            description = "some description"
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
}
