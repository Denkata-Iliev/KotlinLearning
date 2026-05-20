package data

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class Expense(
    val id: String = UUID.randomUUID().toString(),
    val amount: BigDecimal,
    val description: String,
    val date: LocalDate = LocalDate.now(),
    val category: Category
)
