import data.ExpenseRepository

private val repository: ExpenseRepository = ExpenseRepository()
fun main() {
    if (CsvConverter.file.exists()) {
        println("Do you want to load your previous expenses or start anew? y/n")
        val answer = readln().trim().lowercase()
        if (answer == "y") {
            repository.loadFromFile()
        }
    }

    while (true) {
        printMainMenu()

        when (readln().toIntOrNull()) {
            1 -> repository.addExpense()
            2 -> repository.printExpenses()
            3 -> repository.printCategorySummary()
            4 -> repository.filterByDateRange()
            5 -> repository.searchExpense()
            6 -> repository.removeExpense()
            7 -> repository.saveToFile()
            8 -> repository.printStats()
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
        8) Statistics
        0) Exit (empty option also exits)
        ══════════════════════════════════
        Choose an option:
    """.trimIndent()
    )
}
