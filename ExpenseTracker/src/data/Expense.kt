package data

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class Expense(
    private val id: String = UUID.randomUUID().toString(),
    private val amount: BigDecimal,
    private val description: String,
    private val date: LocalDate = LocalDate.now(),
    private val category: Category
)
