import data.Category
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun BigDecimal.asCurrency(): String = "${this}€"

fun LocalDate.toDisplayDate(): String = DATE_DISPLAY_FORMATTER.format(this)

fun Array<Category>.print() = println(
    this.joinToString("\n") { "${it.ordinal + 1}: ${it.label}" }
)