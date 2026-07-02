import org.example.AnswerRecord
import org.example.QuizResult
import org.example.data.Option
import org.example.data.Question
import org.example.dsl.quiz
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class QuizRunnerTests {
    @Test
    fun `correct answer record is correct`() {
        val q = Question(
            question = "Q?",
            explanation = "Explanation",
            options = listOf(
                Option("A", correct = true),
                Option("B")
            ),
            points = 5
        )
        val a = AnswerRecord(q, listOf(0))

        assertTrue(a.correct)
    }

    @Test
    fun `wrong answer record is incorrect`() {
        val q = Question(
            question = "Q?",
            explanation = "Explanation",
            options = listOf(
                Option("A", correct = true),
                Option("B")
            ),
            points = 5
        )
        val a = AnswerRecord(q, listOf(1))

        assertFalse(a.correct)
    }

    @Test
    fun `multiple choice questions requires all options selected to be correct`() {
        val q = Question(
            question = "Q?",
            explanation = "Explanation",
            options = listOf(
                Option("A", correct = true),
                Option("B", correct = true),
                Option("C"),
                Option("D")
            ),
            points = 5
        )
        val allCorrect = AnswerRecord(q, listOf(0, 1))
        val missedOne = AnswerRecord(q, listOf(0, 2))
        val allIncorrect = AnswerRecord(q, listOf(2, 3))

        assertTrue(allCorrect.correct)
        assertFalse(missedOne.correct)
        assertFalse(allIncorrect.correct)
    }

    @Test
    fun `quiz result calculates score, percentage and if passed`() {
        val quiz = quiz("quiz name") {
            description = "Quiz description"
            passingScorePercent = 50
            question("Q?") {
                points = 5
                explanation = "Explanation"
                option("A", correct = true)
                option("B")
            }
            question("Q2?") {
                points = 5
                explanation = "Explanation2"
                option("A", correct = true)
                option("B", correct = true)
                option("C")
                option("D")
            }
            question("Q3?") {
                points = 5
                explanation = "Explanation3"
                option("A", correct = true)
                option("B")
                option("C")
                option("D")
            }
            question("Q4?") {
                points = 5
                explanation = "Explanation4"
                option("A", correct = true)
                option("B")
                option("C")
                option("D")
            }
        }

        val a1 = AnswerRecord(quiz.questions[0], listOf(0))
        val a2 = AnswerRecord(quiz.questions[1], listOf(0, 1))
        val a3 = AnswerRecord(quiz.questions[2], listOf(0, 3))
        val a4 = AnswerRecord(quiz.questions[3], listOf(0, 3))

        val quizResult = QuizResult(quiz, listOf(a1, a2, a3, a4))

        assertEquals(50, quizResult.percentage)
        assertEquals(10, quizResult.score)
        assertTrue(quizResult.passed)
    }
}