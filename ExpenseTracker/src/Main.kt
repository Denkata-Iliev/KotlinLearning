import data.ExpenseRepository
import kotlin.system.exitProcess

private val repository: ExpenseRepository = ExpenseRepository()
fun main() {
    printMainMenu()
    var option = readln().toIntOrNull()

    while (option != null) {
        option = takeActionAndChooseNew(option)
    }
}

fun printMainMenu() {
    println(
        """
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
    """.trimIndent()
    )
}

fun takeActionAndChooseNew(option: Int?): Int? {
    when (option) {
        1 -> repository.addExpense()
        2 -> repository.printExpenses()
        3 -> repository.printCategorySummary()
        0 -> exitProcess(0)
        else -> println("Invalid option. Try again.")
    }

    println("Press Enter key to continue...")
    readln()
    printMainMenu()
    return readln().toIntOrNull()
}
