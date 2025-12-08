import kotlin.math.pow
import kotlin.math.sqrt

fun main() {

    data class Position(val x: Int, val y: Int, val z: Int)

    fun List<String>.parse(): List<Position> {
        return this.map {
            val pos = it.split(",")

            Position(pos[0].toInt(), pos[1].toInt(), pos[2].toInt())
        }
    }

    fun calculateDistance(a: Position, b: Position): Double {
        return sqrt(
            (b.x - a.x).toDouble().pow(2.toDouble())
                    + (b.y - a.y).toDouble().pow(2.toDouble())
                    + (b.z - a.z).toDouble().pow(2.toDouble())
        )
    }

    fun part1(input: List<String>, iteration: Int = 10): Int {
        val positions = input.parse()

        val circuits = mutableListOf<MutableSet<Position>>()
        positions.forEach {
            circuits.add(mutableSetOf(it))
        }

        val pairs = (0..<positions.size).map { i ->
            ((i + 1)..<positions.size).map { j ->
                Triple(positions[i], positions[j], calculateDistance(positions[i], positions[j]))
            }
        }
            .flatMap { it }
            .sortedBy { it.third }
            .toMutableList()

        var connection = 0

        for ((a, b, w) in pairs) {
            val circuitA = circuits.indexOfFirst { it.contains(a) }
            val circuitB = circuits.indexOfFirst { it.contains(b) }

            if (circuitA != circuitB) {
                circuits[circuitA].addAll(circuits[circuitB])
                circuits.removeAt(circuitB)
            }

            connection++

            if (connection == iteration) {
                break
            }
        }

        return circuits.sortedBy { it.size }.reversed()
            .take(3)
            .map { it.size }
            .reduce { a, b -> a * b }
    }

    fun part2(input: List<String>): Long {
        val positions = input.parse()

        val circuits = mutableListOf<MutableSet<Position>>()
        positions.forEach {
            circuits.add(mutableSetOf(it))
        }

        val pairs = (0..<positions.size).map { i ->
            ((i + 1)..<positions.size).map { j ->
                Triple(positions[i], positions[j], calculateDistance(positions[i], positions[j]))
            }
        }
            .flatMap { it }
            .sortedBy { it.third }
            .toMutableList()

        var last: Pair<Position, Position>? = null

        for ((a, b, w) in pairs) {
            val circuitA = circuits.indexOfFirst { it.contains(a) }
            val circuitB = circuits.indexOfFirst { it.contains(b) }

            if (circuitA != circuitB) {
                circuits[circuitA].addAll(circuits[circuitB])
                circuits.removeAt(circuitB)

                last = Pair(a, b)
            }

            if (circuits.size <= 1) {
                break
            }
        }

        return last?.toList()?.map { it.x.toLong() }?.reduce { a, b -> a * b } ?: 0
    }

    // Test if implementation meets criteria from the description, like:
    // check(part1(listOf("test_input")) == 1)

    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("Day08_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 25272L)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput("Day08")
    part1(input, 1000).println()
    part2(input).println()
}
