import java.util.*
import kotlin.streams.asSequence

fun main() {

    data class Button(val targets: List<Int>)
    data class Machine(val lights: List<Boolean>, val buttons: List<Button>, val jolts: List<Int>?)

    fun List<String>.parse(): List<Machine> {
        val lightsRegex = """\[(.*?)]""".toRegex()
        val buttonRegex = """\((.*?)\)""".toRegex()
        val braceRegex = """\{(.*?)}""".toRegex()

        return map { line ->
            val lightString = lightsRegex.find(line)?.groupValues?.get(1) ?: ""
            val lights = lightString.map { it == '#' }

            val buttons = buttonRegex.findAll(line)
                .map { m ->
                    m.groupValues[1]
                        .split(',')
                        .filter { it.isNotBlank() }
                        .map { it.toInt() }
                }
                .map(::Button)
                .toList()

            val jolts = braceRegex.find(line)?.groupValues?.get(1)
                ?.split(',')
                ?.filter { it.isNotBlank() }
                ?.map { it.trim().toInt() }

            Machine(lights, buttons, jolts)
        }
    }

    // --- Part 1 (unchanged) ---
    fun solveLights(m: Machine): Long {
        val n = m.lights.size
        val mButtons = m.buttons.size

        val A = List(n) { r ->
            BitSet(mButtons).apply {
                m.buttons.forEachIndexed { c, btn ->
                    if (r in btn.targets) set(c)
                }
            }
        }.toMutableList()

        val b = m.lights.toMutableList()
        val pivotCol = MutableList(n) { -1 }
        val pivotRow = MutableList(mButtons) { -1 }
        var row = 0

        fun xorRow(i: Int, j: Int) {
            A[i].xor(A[j])
            b[i] = b[i] xor b[j]
        }

        for (col in 0 until mButtons) {
            val pivot = (row until n).firstOrNull { A[it].get(col) } ?: continue
            if (pivot != row) {
                val tmpA = A[row]; A[row] = A[pivot]; A[pivot] = tmpA
                val tmpB = b[row]; b[row] = b[pivot]; b[pivot] = tmpB
            }
            pivotCol[row] = col
            pivotRow[col] = row
            (0 until n).forEach { rIdx ->
                if (rIdx != row && A[rIdx].get(col)) xorRow(rIdx, row)
            }
            row++
            if (row == n) break
        }
        val rank = row
        if ((rank until n).any { A[it].isEmpty && b[it] }) error("No solution")

        val xp = MutableList(mButtons) { false }
        for (i in rank - 1 downTo 0) {
            val pc = pivotCol[i]
            val s = A[i].stream().asSequence()
                .filter { it > pc }
                .fold(false) { acc, j -> acc xor xp[j] }
            xp[pc] = b[i] xor s
        }

        val freeCols = (0 until mButtons).filter { pivotRow[it] == -1 }
        val basis = freeCols.map { free ->
            MutableList(mButtons) { false }.also { vec ->
                vec[free] = true
                for (i in 0 until rank) {
                    val pc = pivotCol[i]
                    val sum = A[i].stream().asSequence()
                        .filter { it != pc }
                        .fold(false) { acc, j -> acc xor vec[j] }
                    vec[pc] = sum
                }
            }
        }

        val k = basis.size
        val best = if (k <= 25) {
            (0 until (1 shl k)).minOf { mask ->
                val cand = xp.toMutableList()
                basis.withIndex().forEach { (bit, vec) ->
                    if ((mask shr bit) and 1 == 1) for (j in cand.indices) cand[j] = cand[j] xor vec[j]
                }
                cand.count { it }
            }
        } else xp.count { it }

        return best.toLong()
    }

    // --- Part 2: exact Dijkstra on bounded integer lattice ---
    fun solveJoltage(m: Machine): Long {
        val target = m.jolts!!
        val n = target.size

        // Each button → List<Int> of 0/1
        val effects: List<List<Int>> = m.buttons.map { btn ->
            List(n) { i -> if (i in btn.targets) 1 else 0 }
        }

        // Sort buttons by descending "impact" to prune more aggressively
        val sorted = effects.sortedByDescending { it.sum() }

        var best = target.sum() // trivial upper bound

        fun add(a: List<Int>, b: List<Int>, k: Int): List<Int>? {
            // adds k * b to a, but returns null if exceeding target
            val res = ArrayList<Int>(n)
            for (i in 0 until n) {
                val v = a[i] + b[i] * k
                if (v > target[i]) return null
                res += v
            }
            return res
        }

        fun dfs(idx: Int, acc: List<Int>, presses: Int) {
            if (presses >= best) return
            if (idx == sorted.size) {
                if (acc == target) best = presses
                return
            }

            val eff = sorted[idx]

            // Compute max times this button may be pressed
            var maxK = Int.MAX_VALUE
            for (i in 0 until n) {
                if (eff[i] == 1) {
                    val room = target[i] - acc[i]
                    if (room < 0) return
                    maxK = minOf(maxK, room)
                }
            }

            // Try biggest k first → find good solution fast → prune early
            for (k in maxK downTo 0) {
                val next = add(acc, eff, k) ?: continue
                dfs(idx + 1, next, presses + k)
            }
        }

        dfs(0, List(n) { 0 }, 0)
        return best.toLong()
    }


    fun part1(input: List<String>): Long =
        input.parse().sumOf { solveLights(it) }

    fun part2(input: List<String>): Long =
        input.parse().sumOf { solveJoltage(it) }

    // --- usage (adjust readInput as in your environment) ---
    val testInput = readInput("Day10_test")
    check(part1(testInput) == 7L)
    check(part2(testInput) == 33L)

    val input = readInput("Day10")
    part1(input).println()
    part2(input).println()
}
