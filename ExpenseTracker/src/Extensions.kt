import data.Category
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.format.DateTimeFormatter

val DATE_DISPLAY_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

fun BigDecimal.asCurrency(): String = "${this}€"

fun LocalDate.toDisplayDate(): String = DATE_DISPLAY_FORMATTER.format(this)

fun Array<Category>.printCategoryOptions() = println(
    this.joinToString("\n") { "${it.ordinal + 1}: ${it.label}" }
)

fun List<BigDecimal>.average(
    scale: Int = 2,
    rounding: RoundingMode = RoundingMode.HALF_UP
): BigDecimal {
    if (isEmpty()) return BigDecimal.ZERO

    return fold(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal(size), scale, rounding)
}