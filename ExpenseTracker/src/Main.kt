import data.Category
import data.Expense
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.system.exitProcess

fun main() {
    printMainMenu()
    var option = readln().toIntOrNull()
    val expenses = mutableListOf<Expense>()

    while (option != null) {
        option = takeActionAndChooseNew(option, expenses)
    }
}

fun printMainMenu() {
    println("""
        ══════════════════════════════════
        💰 EXPENSE TRACKER
        ══════════════════════════════════
        1) Add expense
        2) List all expenses
        3) Summary by category
        4) Filter by date range
        5) Search expenses
        6) Delete expense
        7) Export to CSV
        0) Exit (empty option also exits)
        ══════════════════════════════════
        Choose an option:
    """.trimIndent())
}

fun takeActionAndChooseNew(option: Int?, expenses: MutableList<Expense>): Int? {
    when (option) {
        1 -> addExpense(expenses)
        2 -> println(expenses.joinToString("\n"))
        0 -> exitProcess(0)
        else -> println("chose option 0")
    }

    printMainMenu()
    return readln().toIntOrNull()
}

fun addExpense(expenses: MutableList<Expense>) {
    println("Amount:")
    val amountInput = readlnOrNull()?.toBigDecimalOrNull()
    val amount: BigDecimal? = when {
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
    if (amount == null) {
        return
    }
    println("Description:")
    val description = readln()
    println("Date (leave empty for current date and time) in format YYYY-MM-dd: ")
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
}

fun mapOptionToCategory(option: Int?, categories: Array<Category>): Category {
    return categories.getOrElse((option ?: 1) - 1) {
        categories.first()
    }
}

fun printCategories(categories: Array<Category>) {
    val sb = StringBuilder()
    categories.forEachIndexed { index, category ->
        sb.append("${index + 1}: $category\n")
    }
    println(sb.toString())
}