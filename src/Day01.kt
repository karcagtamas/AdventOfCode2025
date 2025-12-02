import kotlin.math.absoluteValue

fun main() {
    val dialStart = 50;

    fun List<String>.parse(): List<Int> {
        return this.map {
            val prefix = if (it.startsWith('L')) -1 else 1

            it.takeLast(it.length - 1).toInt() * prefix
        }
    }

    fun part1(input: List<String>): Int {
        var sum = dialStart
        var cnt = 0


        for (num in input.parse()) {
            sum += num;
            sum %= 100

            if (sum == 0) {
                cnt++
            }
        }

        return cnt
    }

    fun shift(value: Int): Int {
        var n = value
        while (n !in 0..99) {
            if (n < 0) {
                n += 100
            } else if (value >= 100) {
                n %= 100
            }
        }

        return n
    }

    fun part2(input: List<String>): Int {
        var sum = dialStart
        var cnt = 0

        for (num in input.parse()) {
            val totalTurns = num / 100
            val actualChange = num % 100

            cnt += totalTurns.absoluteValue

            if ((sum + actualChange) !in 1..<100 && sum != 0) {
                cnt++
            }

            sum += actualChange

            sum = shift(sum)
        }

        return cnt
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day01_test")
    check(part1(testInput) == 3)
    check(part2(testInput) == 6)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}
