package data

import asCurrency
import print
import toDisplayDate
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate

// Just so I don't have everything in Main
private const val DESCRIPTION_LENGTH = 70
private val MAX_AMOUNT = BigDecimal(1000)

class ExpenseRepository {
    private val expenses: MutableList<Expense> = mutableListOf()

    fun printExpenses() {
        val rows = expenses.map {
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
            append(TableRenderer.renderTable(headers, rows, true))
            appendLine("Running total: ${getRunningTotal().asCurrency()}")
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

    private fun getRunningTotal() = expenses.sumOf { it.amount }
}