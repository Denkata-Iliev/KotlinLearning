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
        val rows = expenses.map {
            listOf(
                it.id,
                "${it.amount}€",
                it.description.ifBlank { "N/A" },
                it.date.format(formatter),
                it.category.label
            )
        }

        val headers = listOf("ID", "Amount", "Description", "Date", "Category")

        val sb = StringBuilder()

        sb.append(renderTable(headers, rows, true))
        sb.appendLine("Running total: ${getRunningTotal()}€")

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

    fun printCategorySummary() {
        val groups = expenses.groupBy { it.category }

        val runningTotal = getRunningTotal()
        val rows = groups.map { (category, expenses) ->
            val totalExpensesAmount = expenses.sumOf { it.amount }
            val percent = totalExpensesAmount
                .divide(runningTotal)
                .multiply(BigDecimal(100))

            listOf(
                category.label,
                totalExpensesAmount.toString(),
                expenses.size.toString(),
                percent.toString()
            )
        }

        val headers = listOf("Category", "Amount Spent", "Expense Count", "% of Total")

        print(renderTable(headers, rows))
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

    private fun getRunningTotal() = expenses.sumOf { it.amount }

    private fun renderTable(
        headers: List<String>,
        rows: List<List<String>>,
        includeBottomLine: Boolean = false,
    ): String {

        val columnWidths = headers.indices.map { col ->
            maxOf(
                headers[col].length,
                rows.maxOfOrNull { it[col].length } ?: 0
            )
        }

        fun formatRow(row: List<String>): String =
            row.mapIndexed { i, cell ->
                "%-${columnWidths[i]}s".format(cell)
            }.joinToString(
                separator = " | ",
                prefix = "| ",
                postfix = " |"
            )

        val sb = StringBuilder()

        sb.appendLine(formatRow(headers))

        rows.forEach {
            sb.appendLine(formatRow(it))
        }

        if (includeBottomLine) {
            sb.appendLine("—".repeat(formatRow(headers).length))
        }

        return sb.toString()
    }
}