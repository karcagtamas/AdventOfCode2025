import java.util.StringTokenizer

fun main() {

    data class Shape(val cells: Set<Pair<Int, Int>>) {

        fun normalize(): Shape {
            val minX = cells.minOf { it.first }
            val minY = cells.minOf { it.second }

            return Shape(cells.map { (x, y) -> Pair(x - minX, y - minY) }.toSet())
        }

        fun rotate(): Shape {
            return Shape(cells.map { (x, y) -> Pair(-y, x) }.toSet()).normalize()
        }

        fun flip(): Shape {
            return Shape(cells.map { (x, y) -> Pair(-x, y) }.toSet()).normalize()
        }

        fun orientations(): Set<Shape> {
            val result = mutableSetOf<Shape>()

            var s = this.normalize()

            repeat(4) {
                result.add(s)
                result.add(s.flip())
                s = s.rotate()
            }

            return result
        }

        val size: Int get() = cells.size
    }

    data class Region(val w: Int, val h: Int, val counts: List<Int>) {}

    fun List<String>.parse(): Pair<List<Shape>, List<Region>> {
        val shapes = mutableListOf<Shape>()
        var i = 0

        while (i < this.size && this[i].contains(":") && !this[i].contains("x")) {
            i++

            val cells = mutableSetOf<Pair<Int, Int>>()
            var y = 0

            while (i < this.size && this[i].isNotBlank()) {
                for (x in this[i].indices) {
                    if (this[i][x] == '#') {
                        cells.add(Pair(x, y))
                    }
                }

                y++
                i++
            }

            shapes.add(Shape(cells).normalize())
            i++
        }

        val regions = mutableListOf<Region>()
        while (i < this.size) {
            val sec = this[i].split(":")
            val dims = sec[0].trim().split("x")
            val w = dims[0].toInt()
            val h = dims[1].toInt()

            val counts = sec[1].trim().split(" ").map {
                it.trim().toInt()
            }

            regions.add(Region(w, h, counts))
            i++
        }

        return shapes to regions
    }

    fun canPlace(grid: Array<BooleanArray>, shape: Shape, ox: Int, oy: Int): Boolean {
        for ((x, y) in shape.cells) {
            if (grid[oy + y][ox + x]) {
                return false
            }
        }

        return true
    }

    fun place(grid: Array<BooleanArray>, shape: Shape, ox: Int, oy: Int, value: Boolean) {
        for ((x, y) in shape.cells) {
            grid[oy + y][ox + x] = value
        }
    }

    fun canFit(region: Region, orientations: List<List<Shape>>): Boolean {
        val grid = Array(region.h) { BooleanArray(region.w) }
        val pieces = mutableListOf<Int>()

        for (i in region.counts.indices) {
            repeat(region.counts[i]) { pieces.add(i) }
        }

        pieces.sortByDescending { orientations[it][0].size }

        val totalCells = pieces.sumOf { orientations[it][0].size }

        if (totalCells > region.w * region.h) {
            return false
        }

        fun backtrack(idx: Int): Boolean {
            if (idx == pieces.size) {
                return true
            }

            val shapeIndex = pieces[idx]
            for (shape in orientations[shapeIndex]) {
                val maxX = shape.cells.maxOf { it.first }
                val maxY = shape.cells.maxOf { it.second }

                for (y in 0 until region.h - maxY) {
                    for (x in 0 until region.w - maxX) {
                        if (canPlace(grid, shape, x, y)) {
                            place(grid, shape, x, y, true)
                            if (backtrack(idx + 1)) {
                                return true
                            }
                            place(grid, shape, x, y, false)
                        }
                    }
                }
            }

            return false
        }

        return backtrack(0)
    }


    fun part1(input: List<String>): Int {
        val (shapes, regions) = input.parse()

        val allOrientations = shapes.map { it.orientations().toList() }

        var solvable = 0

        for (region in regions) {
            if (canFit(region, allOrientations)) {
                solvable++
            }
        }

        return solvable
    }

    fun part2(input: List<String>): Int {
        return 0
    }

    // --- usage (adjust readInput as in your environment) ---
    val testInput = readInput("Day12_test")
    check(part1(testInput) == 2)
    //check(part2(testInput) == 2)

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}
