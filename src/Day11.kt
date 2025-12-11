fun main() {

    fun List<String>.parse(): Map<String, List<String>> {
        return this.associate { line ->
            val (left, right) = line.split(": ")
            val from = left.trim()
            val to = right.trim().split(" ").filter { it.isNotBlank() }
            from to to
        }
    }

    fun List<String>.parse2(): Map<String, List<String>> {
        val graph = mutableMapOf<String, MutableList<String>>()

        for (line in this) {
            if (line.isBlank()) continue
            val (left, right) = line.split(": ")
            val from = left.trim()
            val to = right.trim().split(" ").filter { it.isNotBlank() }

            graph.putIfAbsent(from, mutableListOf())
            graph[from]!!.addAll(to)

            // ensure ALL target nodes exist
            for (t in to) graph.putIfAbsent(t, mutableListOf())
        }

        return graph.toMap()
    }


    fun part1(input: List<String>): Long {
        val graph = input.parse()

        fun countPaths(node: String, visited: Set<String>): Long {
            if (node == "out") {
                return 1L
            }

            var total = 0L
            for (next in graph[node].orEmpty()) {
                if (next !in visited) {
                    total += countPaths(next, visited + next)
                }
            }

            return total
        }

        return countPaths("you", setOf("you"))
    }

    fun part2(input: List<String>): Long {
        val graph = input.parse2()

        val start = "svr"
        val target = "out"
        val A = "dac"
        val B = "fft"

        val indeg = mutableMapOf<String, Int>()

        // initialize all to zero
        for (node in graph.keys) indeg[node] = 0

        for ((_, outs) in graph) {
            for (o in outs) indeg[o] = indeg[o]!! + 1
        }

        val queue = ArrayDeque<String>()
        for ((k, v) in indeg) if (v == 0) queue.add(k)

        val topo = mutableListOf<String>()

        while (queue.isNotEmpty()) {
            val n = queue.removeFirst()
            topo.add(n)
            for (next in graph[n].orEmpty()) {
                indeg[next] = indeg[next]!! - 1
                if (indeg[next] == 0) queue.add(next)
            }
        }

        fun countPaths(from: String, to: String): Long {
            val dp = mutableMapOf<String, Long>()
            for (n in graph.keys) dp[n] = 0
            dp[from] = 1L

            for (node in topo) {
                val ways = dp[node] ?: 0
                if (ways == 0L) continue
                for (next in graph[node]!!) {
                    dp[next] = dp[next]!! + ways
                }
            }

            return dp[to]!!
        }

        val path1 = countPaths(start, A) * countPaths(A, B) * countPaths(B, target)
        val path2 = countPaths(start, B) * countPaths(B, A) * countPaths(A, target)

        return path1 + path2
    }

    // --- usage (adjust readInput as in your environment) ---
    val testInput = readInput("Day11_test")
    check(part1(testInput) == 5L)

    val testInput2 = readInput("Day11_test2")
    check(part2(testInput2) == 2L)

    val input = readInput("Day11")
    part1(input).println()
    part2(input).println()
}
