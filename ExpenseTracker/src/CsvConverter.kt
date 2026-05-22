import data.Category
import data.Expense
import java.io.File
import java.time.LocalDate

object CsvConverter {
    val file: File = File("expenses.csv")

    fun serialize(expense: Expense): String {
        val sanitizedDescription = expense.description.replace(",", " ")
        return "${expense.id},${expense.amount},${sanitizedDescription},${expense.date.toDisplayDate()},${expense.category.name}"
    }

    fun deserialize(data: String): Expense? {
        val split = data.split(",")
        return runCatching {
            Expense(
                id = split[0],
                amount = split[1].toBigDecimal(),
                description = split[2],
                date = LocalDate.parse(split[3], DATE_DISPLAY_FORMATTER),
                category = Category.valueOf(split[4])
            )
        }.getOrNull()
    }
}