fun main() {
    fun List<String>.parse(): List<LongRange> {
        return this.joinToString(",")
            .split(",")
            .map {
                val border = it.split("-")

                border[0].toLong()..border[1].toLong()
            }
    }

    fun part1(input: List<String>): Long {
        return input
            .parse()
            .flatMap {
                it
                    .filter { n ->
                        n.toString().length % 2 == 0
                    }
                    .filter { n ->
                        val s = n.toString()

                        s.take(s.length / 2) == s.substring(s.length / 2)
                    }
            }
            .sum()
    }


    fun part2(input: List<String>): Long {
        return input
            .parse()
            .flatMap {
                it
                    .filter { n ->
                        val s = n.toString()

                        (1..(s.length / 2))
                            .any { x ->
                                s.take(x).repeat(s.length / x) == s
                            }
                    }
            }
            .sum()
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day02_test")
    check(part1(testInput) == 1227775554L)
    check(part2(testInput) == 4174379265L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}
