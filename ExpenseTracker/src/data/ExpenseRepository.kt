package data

import java.math.BigDecimal
import java.time.LocalDate

// Just so I don't have everything in Main
class ExpenseRepository {
    private val expenses: MutableList<Expense> = mutableListOf()

    fun printExpenses() = println(expenses.joinToString("\n"))

    fun addExpense() {
        println("Amount:")
        val amount = inputAmount() ?: return

        println("Description:")
        val description = readln()

        println("Date (leave empty for today) in format YYYY-MM-dd: ")
        val dateInput = readlnOrNull()
        val date = if (dateInput.isNullOrEmpty()) LocalDate.now() else LocalDate.parse(dateInput)

        println("Category - choose a number from the list. If no or invalid option is chosen, the first from the list will be the default:")
        val categories = Category.entries.toTypedArray()
        printCategories(categories)
        val category = mapOptionToCategory(readln().toIntOrNull(), categories)

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
        val amountInput = readlnOrNull()?.toBigDecimalOrNull()
        return when {
            amountInput == null -> {
                println("You need to enter a valid amount")
                null
            }

            amountInput <= BigDecimal.ZERO -> {
                println("You need to enter a valid amount")
                null
            }

            amountInput > BigDecimal.valueOf(1000) -> {
                println("You don't have that kind of money, bro")
                null
            }

            else -> amountInput
        }
    }

    private fun mapOptionToCategory(option: Int?, categories: Array<Category>): Category {
        return categories.getOrElse((option ?: 1) - 1) {
            categories.first()
        }
    }

    private fun printCategories(categories: Array<Category>) {
        val sb = StringBuilder()
        categories.forEachIndexed { index, category ->
            sb.append("${index + 1}: $category\n")
        }
        print(sb.toString())
    }
}