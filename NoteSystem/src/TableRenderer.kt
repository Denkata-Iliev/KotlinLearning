object TableRenderer {
    fun renderTable(
        headers: List<String>,
        rows: List<List<String>>,
        includeBottomLine: Boolean = false,
    ): String {

        val columnWidths = headers.indices.map { col ->
            maxOf(
                headers[col].length,
                rows.maxOfOrNull { it[col].length } ?: 0
            )
        }

        fun formatRow(row: List<String>): String =
            row.mapIndexed { i, cell ->
                "%-${columnWidths[i]}s".format(cell)
            }.joinToString(
                separator = " | ",
                prefix = "| ",
                postfix = " |"
            )

        return buildString {
            appendLine(formatRow(headers))
            rows.forEach {
                appendLine(formatRow(it))
            }

            if (includeBottomLine) {
                appendLine("—".repeat(formatRow(headers).length))
            }
        }
    }
}