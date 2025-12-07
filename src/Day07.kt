sealed class Field {

    object Splitter : Field()

    object Start : Field()

    object Fall : Field()
}

fun main() {

    fun List<String>.parse(): List<List<Field?>> {
        val fields = this
            .map { row ->
                row.split("")
                    .map {
                        when (it) {
                            "S" -> {
                                Field.Start
                            }

                            "^" -> {
                                Field.Splitter
                            }

                            else -> {
                                null
                            }
                        }
                    }
                    .toMutableList()
            }

        (1..<fields.size).forEach { rowIdx ->
            (0..<fields[rowIdx].size).forEach { idx ->
                if (fields[rowIdx][idx] == null && (fields[rowIdx - 1][idx] is Field.Start || fields[rowIdx - 1][idx] is Field.Fall)) {
                    fields[rowIdx][idx] = Field.Fall
                }

                if (fields[rowIdx][idx] is Field.Splitter) {
                    if (idx > 0) {
                        fields[rowIdx][idx - 1] = Field.Fall
                    }

                    if (idx < fields.size - 1) {
                        fields[rowIdx][idx + 1] = Field.Fall
                    }
                }
            }
        }

        return fields
    }

    fun part1(input: List<String>): Int {
        val fields = input.parse()
        return fields
            .mapIndexed { rowIdx, row ->
                row
                    .filterIndexed { idx, field ->
                        field is Field.Splitter && fields[rowIdx - 1][idx] is Field.Fall
                    }
                    .count()
            }
            .sum()
    }

    fun paths(rowIdx: Int, idx: Int, fields: List<List<Field?>>): Int {
        if (rowIdx >= fields.lastIndex) {
            return 1
        }

        if (fields[rowIdx][idx] is Field.Start || fields[rowIdx][idx] is Field.Fall) {
            return paths(rowIdx, idx, fields.drop(1))
        }

        if (fields[rowIdx][idx] is Field.Splitter) {
            return paths(rowIdx, idx - 1, fields) + paths(rowIdx, idx + 1, fields)
        }

        return 0
    }

    fun paths2(fields: List<List<Field?>>): Long {
        val rows = fields.size
        val cols = fields[0].size

        val dp = Array(rows) { LongArray(cols) }

        for (c in 0 until cols) {
            if (fields[rows - 1][c] is Field.Fall) {
                dp[rows - 1][c] = 1
            }
        }

        for (r in rows - 2 downTo 0) {

            // First: vertical propagation from below
            for (c in 0 until cols) {
                if (fields[r][c] is Field.Start || fields[r][c] is Field.Fall) {
                    dp[r][c] = dp[r + 1][c]
                }
            }

            // Then: resolve ^ splitters left → right
            for (c in 0 until cols) {
                if (fields[r][c] is Field.Splitter) {
                    var count = 0L

                    // Go left until a Fall or border
                    var i = c - 1
                    while (i >= 0 && fields[r][i] !is Field.Fall) i--
                    if (i >= 0) count += dp[r + 1][i]

                    // Go right until a Fall or border
                    i = c + 1
                    while (i < cols && fields[r][i] !is Field.Fall) i++
                    if (i < cols) count += dp[r + 1][i]

                    dp[r][c] = count
                }
            }
        }

        val startCol = fields[0].indexOf(Field.Start)
        return dp[0][startCol]
    }

    fun part2(input: List<String>): Long {
        val fields = input.parse()
        return paths2(fields)
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 21)
    check(part2(testInput) == 40L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day07")
    part1(input).println()
    part2(input).println()
}
