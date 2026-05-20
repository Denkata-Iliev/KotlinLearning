package data

import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Just so I don't have everything in Main
private const val DESCRIPTION_LENGTH = 70
private const val MAX_AMOUNT = 1000
class ExpenseRepository {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    private val expenses: MutableList<Expense> = mutableListOf()

    fun printExpenses() {
        data class Row(
            val id: String,
            val amount: String,
            val description: String,
            val date: String,
            val category: String
        )

        val rows = expenses.map {
            Row(
                id = it.id,
                amount = "${it.amount}€",
                description = it.description.ifBlank { "N/A" },
                date = it.date.format(formatter),
                category = it.category.label
            )
        }

        val headers = Row("ID", "Amount", "Description", "Date", "Category")

        fun maxWidth(selector: (Row) -> String): Int =
            maxOf(
                selector(headers).length,
                rows.maxOfOrNull { selector(it).length } ?: 0
            )

        val wId = maxWidth { it.id }
        val wAmount = maxWidth { it.amount }
        val wDesc = maxWidth { it.description }
        val wDate = maxWidth { it.date }
        val wCat = maxWidth { it.category }

        fun fmt(r: Row): String =
            "| %-${wId}s | %-${wAmount}s | %-${wDesc}s | %-${wDate}s | %-${wCat}s |".format(
                r.id, r.amount, r.description, r.date, r.category
            )

        val sb = StringBuilder()

        sb.appendLine(fmt(headers))

        rows.forEach { sb.appendLine(fmt(it)) }

        val separator = "—".repeat(fmt(headers).trimEnd().length)
        sb.appendLine(separator)
        sb.appendLine("Running total: ${expenses.sumOf { it.amount }}€")

        print(sb)
    }

    fun addExpense() {
        println("Amount:")
        val amount = inputAmount() ?: return

        println("Description:")
        val description = readln().takeIf { it.isBlank() || it.length < DESCRIPTION_LENGTH }
        if (description == null) {
            println("The description can't exceed $DESCRIPTION_LENGTH characters")
            return
        }

        println("Date (leave empty for today) in format YYYY-MM-dd: ")
        val dateInput = readlnOrNull()
        val date = if (dateInput.isNullOrEmpty()) LocalDate.now() else LocalDate.parse(dateInput)

        val category = inputCategory()

        expenses.add(
            Expense(
                amount = amount,
                description = description,
                date = date,
                category = category
            )
        )
        println("Successfully added expense")
    }

    private fun inputAmount(): BigDecimal? {
        val amountInput = readlnOrNull()
            ?.toBigDecimalOrNull()
            ?.takeIf {
                it > BigDecimal.ZERO && it <= BigDecimal(MAX_AMOUNT)
            }

        return when (amountInput) {
            null -> {
                println("You need to enter a valid amount (between 1 and $MAX_AMOUNT)")
                null
            }

            else -> amountInput
        }
    }

    private fun inputCategory(): Category {
        println("Category - choose a number from the list. If no or invalid option is chosen, default is 'Other':")
        val categories = Category.entries.toTypedArray()
        printCategories(categories)

        val index = readln().toIntOrNull()?.minus(1) ?: return Category.OTHER
        return categories.getOrElse(index) {
            Category.OTHER
        }
    }

    private fun printCategories(categories: Array<Category>) {
        val sb = StringBuilder()
        categories.forEachIndexed { index, category ->
            sb.appendLine("${index + 1}: ${category.label}")
        }
        print(sb.toString())
    }
}