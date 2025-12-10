import kotlin.math.absoluteValue

fun main() {

    data class Coord(val x: Long, val y: Long)

    fun List<String>.parse(): List<Coord> {
        return this.map {
            val pos = it.split(",")

            Coord(pos[0].toLong(), pos[1].toLong())
        }
    }

    fun part1(input: List<String>): Long {
        val coords = input.parse()

        return (0..<(coords.size - 1)).flatMap { i ->
            ((i + 1)..<(coords.size)).map { j ->
                ((coords[i].x - coords[j].x).absoluteValue + 1) * ((coords[i].y - coords[j].y).absoluteValue + 1)
            }
        }.max()
    }

    fun part2(input: List<String>): Long {
        val red = input.parse()  // Your existing parser

        // ----------------------------------------------------
        // Build list of all edges
        // ----------------------------------------------------
        val edges = mutableListOf<Pair<Coord, Coord>>()
        for (i in red.indices) {
            val a = red[i]
            val b = red[(i + 1) % red.size]
            edges += a to b
        }

        // ----------------------------------------------------
        // Scanline crossing: For each Y, store X where boundary is crossed vertically
        // ----------------------------------------------------
        val cross = mutableMapOf<Long, MutableList<Long>>()  // Y -> list of X crossings
        val horiz = mutableSetOf<Triple<Long, Long, Long>>()  // horizontal edges (y, x1, x2)

        for ((a, b) in edges) {
            if (a.y == b.y) {
                // horizontal edge
                val y = a.y
                val x1 = minOf(a.x, b.x)
                val x2 = maxOf(a.x, b.x)
                horiz += Triple(y, x1, x2)
            } else {
                // vertical edge
                val x = a.x
                val y1 = minOf(a.y, b.y)
                val y2 = maxOf(a.y, b.y)
                for (y in y1 until y2) {
                    cross.computeIfAbsent(y) { mutableListOf() }.add(x)
                }
            }
        }

        // ----------------------------------------------------
        // Build interior intervals per row
        // ----------------------------------------------------
        data class Interval(val start: Long, val end: Long)

        val allowed = mutableMapOf<Long, MutableList<Interval>>() // y -> intervals

        for ((y, xs) in cross) {
            val list = xs.sorted()

            val row = mutableListOf<Interval>()
            var i = 0
            while (i + 1 < list.size) {
                val x1 = list[i]
                val x2 = list[i + 1]
                // interior is between crossings, excluding boundary itself
                if (x2 > x1) {
                    row += Interval(x1, x2)
                }
                i += 2
            }
            allowed[y] = row
        }

        // Add horizontal boundary edges as allowed too
        for ((y, x1, x2) in horiz) {
            allowed.computeIfAbsent(y) { mutableListOf() }
                .add(Interval(x1, x2))
        }

        // Sort intervals per row (merge overlapping)
        for ((y, list) in allowed) {
            val merged = mutableListOf<Interval>()
            val sorted = list.sortedBy { it.start }
            var cur = sorted[0]

            for (k in 1 until sorted.size) {
                val nxt = sorted[k]
                if (nxt.start <= cur.end) {
                    cur = Interval(cur.start, maxOf(cur.end, nxt.end))
                } else {
                    merged += cur
                    cur = nxt
                }
            }
            merged += cur
            allowed[y] = merged
        }

        // ----------------------------------------------------
        // Helper: check if full [x1,x2] is inside the allowed intervals at row y
        // ----------------------------------------------------
        fun rowAllows(y: Long, x1: Long, x2: Long): Boolean {
            val ints = allowed[y] ?: return false
            for (iv in ints) {
                if (x1 >= iv.start && x2 <= iv.end) return true
            }
            return false
        }

        // ----------------------------------------------------
        // Test rectangles
        // ----------------------------------------------------
        var best = 0L

        for (i in red.indices) {
            for (j in i + 1 until red.size) {
                val a = red[i]
                val b = red[j]
                if (a.x == b.x || a.y == b.y) continue

                val x1 = minOf(a.x, b.x)
                val x2 = maxOf(a.x, b.x)
                val y1 = minOf(a.y, b.y)
                val y2 = maxOf(a.y, b.y)

                var ok = true
                for (y in y1..y2) {
                    if (!rowAllows(y, x1, x2)) {
                        ok = false
                        break
                    }
                }

                if (ok) {
                    val area = (x2 - x1 + 1L) * (y2 - y1 + 1L)
                    if (area > best) best = area
                }
            }
        }

        return best
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day09_test")
    check(part1(testInput) == 50L)
    check(part2(testInput) == 24L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}
