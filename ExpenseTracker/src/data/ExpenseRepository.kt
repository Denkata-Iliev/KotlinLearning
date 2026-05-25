package data

import CsvConverter
import TableRenderer
import asCurrency
import average
import printCategoryOptions
import toDisplayDate
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Just so I don't have everything in Main
private const val DESCRIPTION_LENGTH = 70
private val MAX_AMOUNT = BigDecimal(1000)

class ExpenseRepository {
    private val expenses: MutableList<Expense> = mutableListOf()

    fun printStats() {
        println("""
            ════════════════════════════
                    📈 STATISTICS
            ════════════════════════════
        """.trimIndent())
        if (expenses.isEmpty()) {
            println("You don't have any expenses yet, so there is nothing to show here.")
            return
        }
        // total exp count
        val totalExpCount = expenses.size
        println("Total expenses: $totalExpCount")

        // running total
        val total = getRunningTotal()
        println("Total spent: ${total.asCurrency()}")
        println()

        // avg exp
        val avg = expenses.map { it.amount }.average()
        println("Average expense: ${avg.asCurrency()}")

        // avg daily exp
        val now = LocalDate.now()
        val thisYearExpenses = expenses.filter { it.date.year == now.year }
        val totalThisYear = thisYearExpenses.sumOf { it.amount }

        val activeDays = thisYearExpenses.distinctBy { it.date }.size
        val avgDaily =
            if (activeDays == 0) BigDecimal.ZERO
            else totalThisYear.divide(BigDecimal(activeDays), 2, RoundingMode.HALF_UP)

        println("Average daily expense: ${avgDaily.asCurrency()}")
        println()

        // highest exp
        // lowest exp
        val (highest, lowest) = getHighestLowestExpense()
        println("Highest expense: ${highest.amount.asCurrency()} (${highest.description})")
        println("Lowest expense: ${lowest.amount.asCurrency()} (${lowest.description})")
        println()

        val groupByCategory = expenses.groupBy { it.category }
        // most expensive cat
        val categoryAmountPair = groupByCategory.map {
            val amount = it.value.sumOf { exp -> exp.amount }
            Pair(it.key, amount)
        }.maxBy { it.second }
        println("Most expensive category: ${categoryAmountPair.first.label} - ${categoryAmountPair.second.asCurrency()}")
        /* Alternative, more Kotlin-ish way:
        val categoryAmountPair1 = expenses
            .groupBy { it.category }
            .mapValues { (_, exps) -> exps.sumOf { it.amount } }
            .maxBy { it.value } */

        // most used category
        val categoryUseCountPair = groupByCategory
            .mapValues { it.value.size }
            .maxBy { it.value }
        println("Most used category: ${categoryUseCountPair.key.label} - ${categoryUseCountPair.value} expenses")
        println()

        // this month total
        val monthTotal = expenses.filter { it.date.month == now.month && it.date.year == now.year }.sumOf { it.amount }
        println("Month total: ${monthTotal.asCurrency()}")
        // this year total
        println("Year total: ${totalThisYear.asCurrency()}")
        println()

        // top 5 expenses
        println("""
            Top 5 expenses
            ────────────────────
        """.trimIndent())
        val topFiveExpenses = expenses
            .sortedByDescending { it.amount }
            .take(5)
            .joinToString("\n") { "${it.amount.asCurrency()} - ${it.description}" }
        println(topFiveExpenses)

        println()
        /*
        * Monthly comparison
        ────────────────────
        Jan 2026: 10
        Feb 2026: 70
        Mar 2026: 150
        Apr 2026: 79
        May 2026: 449
                * */
        println("""
            Monthly comparison
            ────────────────────
        """.trimIndent())
        val locale = Locale.ENGLISH
        val monthlyComparison = expenses.filter { it.date.year == now.year }
            .groupBy { it.date.month }
            .mapValues { (_, exps) -> exps.sumOf { it.amount } }
            .toSortedMap()
            .toList()
            .joinToString("\n") { "${it.first.getDisplayName(TextStyle.SHORT, locale)}: ${it.second.asCurrency()}" }
        println(monthlyComparison)
    }

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

        val (startDate, endDate) = when (readln().toIntOrNull()) {
            1 -> inputStartEndDate() ?: return
            2 -> LocalDate.now().minusWeeks(1) to LocalDate.now()
            3 -> LocalDate.now().minusMonths(1) to LocalDate.now()
            4 -> LocalDate.now().minusYears(1) to LocalDate.now()
            else -> {
                println("Welp...")
                return
            }
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
        val showRunningTotal = expensesToPrint == null || expensesToPrint.isNotEmpty()
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
        categories.printCategoryOptions()

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

        return startDate to endDate
    }

    private fun getHighestLowestExpense(): Pair<Expense, Expense> {
        val max = expenses.maxBy { it.amount }
        val min = expenses.minBy { it.amount }

        return max to min
    }

    private fun getRunningTotal() = expenses.sumOf { it.amount }
}