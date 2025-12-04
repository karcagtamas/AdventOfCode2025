fun main() {
    data class Point(val x: Int, val y: Int)

    fun List<MutableList<Boolean>>.getOrNull(p: Point): Boolean? {

        if (p.x < 0 || p.x >= this.size) {
            return null
        }

        val row = this[p.x]

        if (p.y < 0 || p.y >= row.size) {
            return null
        }

        return row[p.y]
    }

    fun adjacents(p: Point): List<Point> {
        return ((p.x - 1)..(p.x + 1))
            .flatMap { x ->
                ((p.y - 1)..(p.y + 1))
                    .filter { y ->
                        x != p.x || y != p.y
                    }
                    .map { y ->
                        Point(x, y)
                    }
            }
    }

    fun List<String>.parse(): List<MutableList<Boolean>> {
        return this
            .map {
                it.map { c ->
                    c == '@'
                }.toMutableList()
            }
    }

    fun removable(t: List<MutableList<Boolean>>): List<Point> {
        return (0..<t.size)
            .flatMap { x ->
                (0..<t[x].size)
                    .map { y ->
                        Point(x, y)
                    }
                    .filter {
                        val v = t.getOrNull(it)
                        v != null && v
                    }
                    .filter { p ->
                        adjacents(p)
                            .map { t.getOrNull(it) }
                            .filter { it != null }
                            .count { it == true } < 4
                    }
            }
    }

    fun part1(input: List<String>): Int {
        return removable(input.parse())
            .count()
    }

    fun part2(input: List<String>): Int {
        val t = input.parse()
        var removed = 0
        var lastRemoved = 0

        do {
            val removable = removable(t)

            removable.forEach { p ->
                t[p.x][p.y] = false
            }

            removed += removable.count()
            lastRemoved = removable.count()
        } while (lastRemoved > 0)

        return removed
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day04_test")
    check(part1(testInput) == 13)
    check(part2(testInput) == 43)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}
