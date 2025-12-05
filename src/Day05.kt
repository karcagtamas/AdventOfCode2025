fun main() {
    fun List<String>.parse(): Pair<List<LongRange>, List<Long>> {
        var idx = 0
        val ranges = mutableListOf<LongRange>()
        val ids = mutableListOf<Long>()

        do {
            val borders = this[idx].split("-")
            ranges.add(borders[0].toLong()..borders[1].toLong())
            idx++
        } while (this.size > idx && this[idx].isNotEmpty())

        idx++

        do {
            ids.add(this[idx].toLong())
            idx++
        } while (this.size > idx)

        return Pair(ranges.toList(), ids.toList())
    }

    fun part1(input: List<String>): Int {
        val db = input.parse()

        return db.second.count { n ->
            db.first.any { range ->
                n in range
            }
        }
    }

    fun List<LongRange>.union(): List<LongRange> {
        if (this.isEmpty()) {
            return emptyList()
        }

        val sorted = this.sortedBy { it.first }
        val result = mutableListOf<LongRange>()

        var current = sorted[0]

        for (next in sorted.drop(1)) {
            current = if (next.first <= current.last + 1) {
                current.first..maxOf(current.last, next.last)
            } else {
                result.add(current)
                next
            }
        }

        result.add(current)

        return result
    }

    fun part2(input: List<String>): Long {
        val db = input.parse()

        return db.first.union()
            .sumOf {
                it.last - it.first + 1
            }
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day05_test")
    check(part1(testInput) == 3)
    check(part2(testInput) == 14L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()
}
