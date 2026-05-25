package data

import CsvConverter
import TableRenderer
import asCurrency
import print
import toDisplayDate
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Just so I don't have everything in Main
private const val DESCRIPTION_LENGTH = 70
private val MAX_AMOUNT = BigDecimal(1000)

class ExpenseRepository {
    private val expenses: MutableList<Expense> = mutableListOf()

    fun filterByDateRange() {
        println("Filter by:")
        println(
            """
            1. Date range (start to end date)
            2. This week
            3. This month
            4. This year
        """.trimIndent()
        )

        var startDate: LocalDate
        var endDate: LocalDate? = null
        when (readln().toIntOrNull()) {
            1 -> inputStartEndDate()?.let { (s, e) ->
                startDate = s
                endDate = e
            } ?: return

            2 -> startDate = LocalDate.now().minusWeeks(1)
            3 -> startDate = LocalDate.now().minusMonths(1)
            4 -> startDate = LocalDate.now().minusYears(1)
            else -> {
                println("Welp...")
                return
            }
        }

        if (endDate == null) {
            endDate = LocalDate.now()
        }

        val result = expenses.filter { it.date in startDate..endDate }
        printExpenses(result)
    }

    fun searchExpense() {
        println("Search:")
        val input = readln().trim().lowercase()

        val result = expenses.filter { it.description.contains(input, true) }
        printExpenses(result)
    }

    fun printExpenses(expensesToPrint: List<Expense>? = null) {
        // if showing search results, we don't want to show the running total.
        // it should be shown only when listing ALL expenses
        val showRunningTotal = expensesToPrint?.size?.let { it <= 0 } ?: true
        val list = expensesToPrint ?: expenses
        val rows = list.map {
            listOf(
                it.id,
                it.amount.asCurrency(),
                it.description.ifBlank { "N/A" },
                it.date.toDisplayDate(),
                it.category.label
            )
        }

        val headers = listOf("ID", "Amount", "Description", "Date", "Category")

        val str = buildString {
            append(TableRenderer.renderTable(headers, rows, showRunningTotal))
            if (showRunningTotal) {
                appendLine("Running total: ${getRunningTotal().asCurrency()}")
            }
        }

        print(str)
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
        val dateInput = readln()
        val date = if (dateInput.isEmpty()) LocalDate.now() else LocalDate.parse(dateInput)

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
            val percent = if (runningTotal > BigDecimal.ZERO) {
                totalExpensesAmount
                    .multiply(BigDecimal(100))
                    .divide(runningTotal, 2, RoundingMode.HALF_UP)
            } else {
                BigDecimal.ZERO
            }

            listOf(
                category.label,
                totalExpensesAmount.toString(),
                expenses.size.toString(),
                percent.toString()
            )
        }

        val headers = listOf("Category", "Amount Spent", "Expense Count", "% of Total")

        print(TableRenderer.renderTable(headers, rows))
    }

    fun removeExpense() {
        val id = inputId()
        if (id == null) {
            println("Input a valid id. Use the 'list' option and copy the id.")
            return
        }

        val index = expenses.indexOfFirst { it.id == id }
        if (index == -1) {
            println("Could not find expense with id: $id")
            return
        }
        expenses.removeAt(index)
        println("Successfully removed expense")
    }

    fun saveToFile() {
        if (!CsvConverter.file.exists()) {
            CsvConverter.file.createNewFile()
        }

        CsvConverter.file.writeText(expenses.joinToString("\n") { CsvConverter.serialize(it) })
        println("Saved expenses to file")
    }

    fun loadFromFile() {
        expenses.clear()
        val loaded = CsvConverter.file
            .readLines()
            .filter { it.isNotBlank() }
            .mapNotNull { CsvConverter.deserialize(it) }
            .toMutableList()

        expenses.addAll(loaded)
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun inputId(): String? {
        println("Id:")
        val id = readln().takeIf { it.isNotBlank() && Uuid.parseOrNull(it) != null }
        return id
    }

    private fun inputAmount(): BigDecimal? {
        val amountInput = readln()
            .toBigDecimalOrNull()

        return when {
            amountInput == null -> {
                println("You need to enter a valid amount (between 1 and $MAX_AMOUNT)")
                null
            }

            amountInput <= BigDecimal.ZERO -> {
                println("Don't fuck with me, put an actual amount!")
                null
            }

            amountInput > MAX_AMOUNT -> {
                println("You don't have that kind of money, bro!")
                null
            }

            else -> amountInput
        }
    }

    private fun inputCategory(): Category {
        println("Category - choose a number from the list. If no or invalid option is chosen, default is 'Other':")
        val categories = Category.entries.toTypedArray()
        categories.print()

        val index = readln().toIntOrNull()?.minus(1) ?: return Category.OTHER
        return categories.getOrElse(index) {
            Category.OTHER
        }
    }

    private fun inputStartEndDate(): Pair<LocalDate, LocalDate>? {
        println("Start date (YYY-MM-dd):")
        val startInput = readln()
        val startDate = if (startInput.isEmpty()) null else LocalDate.parse(startInput)
        println("End date (YYY-MM-dd):")
        val endInput = readln()
        val endDate = if (endInput.isEmpty()) null else LocalDate.parse(endInput)

        if (startDate == null || endDate == null) {
            return null
        }

        return Pair(startDate, endDate)
    }

    private fun getRunningTotal() = expenses.sumOf { it.amount }
}