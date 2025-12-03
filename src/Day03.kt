fun main() {
    fun List<Int>.toNumber(): Long {
        return this
            .joinToString("") { it.toString() }
            .toLong()
    }

    fun List<String>.parse(): List<List<Int>> {
        return this
            .map {
                it.map { c ->
                    c.digitToInt()
                }
            }
    }

    fun part1(input: List<String>): Long {
        return input.parse()
            .map { bank ->
                (0..<(bank.size - 1))
                    .flatMap { i ->
                        ((i + 1)..<bank.size)
                            .map { j ->
                                listOf(bank[i], bank[j]).toNumber()
                            }
                    }
            }
            .sumOf {
                it.sorted().max()
            }
    }

    fun combinations(list: List<Int>, k: Int): List<Long> {
        if (k == 0) {
            return emptyList()
        }

        if (list.size < k) {
            return emptyList()
        }

        if (list.size == k) {
            return listOf(list.toNumber())
        }

        val head = list.first()
        val tail = list.drop(1)

        return combinations(tail, k) + combinations(tail, k - 1).flatMap { listOf(head.toLong()) + it }
    }

    fun maxSubsequence(list: List<Int>, k: Int): Long {
        val stack = ArrayDeque<Int>()
        var toRemove = list.size - k

        for (digit in list) {
            while (
                stack.isNotEmpty()
                && toRemove > 0
                && stack.last() < digit
            ) {
                stack.removeLast()
                toRemove--
            }

            stack.addLast(digit)
        }

        return stack.take(k).toNumber()
    }

    fun part2(input: List<String>): Long {
        return input.parse()
            .map { maxSubsequence(it, 12) }
            .sumOf { it }
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day03_test")
    check(part1(testInput) == 357L)
    //check(part2(testInput) == 3121910778619)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}
