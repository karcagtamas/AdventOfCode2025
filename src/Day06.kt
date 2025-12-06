sealed class Operator {

    abstract fun operate(x: Long, y: Long): Long

    object Addition : Operator() {
        override fun operate(x: Long, y: Long): Long {
            return x + y
        }
    }

    object Multiplication : Operator() {
        override fun operate(x: Long, y: Long): Long {
            return x * y
        }
    }
}

fun main() {

    data class Problem(val numbers: List<Long>, val operator: Operator) {
        fun calculate(): Long {
            return numbers.reduce { a, b -> operator.operate(a, b) }
        }
    }

    fun String.toOperator(): Operator {
        return when {
            this == "+" -> {
                Operator.Addition
            }

            this == "*" -> {
                Operator.Multiplication
            }

            else -> {
                throw IllegalArgumentException("Invalid operator: $this")
            }
        }
    }

    fun List<String>.parse(): List<Problem> {
        val rows = this.map { it.trim().split(Regex("""\s+""")) }
        val problems = mutableListOf<Problem>()

        for (i in 0..rows.last().lastIndex) {
            problems.add(
                Problem(
                    (0..<rows.lastIndex).map { rows[it][i].toLong() },
                    rows.last()[i].toOperator()
                )
            )
        }

        return problems.toList()
    }

    fun List<String>.equalLength(): List<String> {
        val max = this.maxOf { it.length }

        return this.map {
            it + " ".repeat(max - it.length)
        }
    }

    fun List<String>.parse2(): List<Problem> {
        val rows = this.take(this.size - 1).equalLength()
        val operators = this.last() + " ".repeat(rows.first().length - this.last().length)

        val problems = mutableListOf<Problem>()

        val numbers = mutableListOf<Long>()
        for (i in (0..operators.lastIndex).reversed()) {
            val number = rows.map { it[i] }.joinToString("").trim()

            if (number == "") {
                continue
            }

            numbers.add(number.toLong())

            if (operators[i] == '+' || operators[i] == '*') {
                problems.add(Problem(numbers.toList(), operators[i].toString().toOperator()))
                numbers.clear()
            }
        }

        return problems.toList()
    }

    fun part1(input: List<String>): Long {
        return input.parse().sumOf { it.calculate() }
    }

    fun part2(input: List<String>): Long {
        return input.parse2().sumOf { it.calculate() }
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day06_test")
    check(part1(testInput) == 4277556L)
    check(part2(testInput) == 3263827L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}
