import org.example.dsl.quiz
import kotlin.test.Test
import kotlin.test.assertEquals

class QuizBuilderTests {
    @Test
    fun `quiz has correct name`() {
        val q = quiz("some name") {}
        assertEquals("some name", q.title)
    }

    @Test
    fun `quiz has description`() {
        val q = quiz("some name") {
            description = "some description"
        }

        assertEquals("some description", q.description)
    }

    @Test
    fun `quiz has correct passingScorePercent`() {
        val q = quiz("some name") {
            passingScorePercent = 60
        }

        assertEquals(60, q.passingScorePercent)
    }
}
