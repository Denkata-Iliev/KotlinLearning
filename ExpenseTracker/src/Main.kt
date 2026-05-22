import data.ExpenseRepository

private val repository: ExpenseRepository = ExpenseRepository()
fun main() {
    while (true) {
        printMainMenu()

        when (readln().toIntOrNull()) {
            1 -> repository.addExpense()
            2 -> repository.printExpenses()
            3 -> repository.printCategorySummary()
            6 -> repository.removeExpense()
            0, null -> break
            else -> println("Invalid option. Try again.")
        }

        println("Press Enter to continue...")
        readln()
    }
}

private fun printMainMenu() {
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
