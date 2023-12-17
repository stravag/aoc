package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.PrintColor
import ch.ranil.aoc.aoc2023.Direction.*
import ch.ranil.aoc.print
import ch.ranil.aoc.printColor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day17 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(102, compute1(testInput))
    }

    @Test
    fun neighborsTest() {
        SearchState(Point(0, 0), E, sameDirectionCount = 0).let {
            assertEquals(it.neighbors(), it.neighbors2())
        }

        SearchState(Point(0, 0), E, sameDirectionCount = 1).let {
            assertEquals(it.neighbors(), it.neighbors2())
        }

        SearchState(Point(0, 0), E, sameDirectionCount = 2).let {
            assertEquals(it.neighbors(), it.neighbors2())
        }

        SearchState(Point(0, 0), E, sameDirectionCount = 3).let {
            assertEquals(it.neighbors(), it.neighbors2())
        }

        SearchState(Point(0, 1), S, sameDirectionCount = 0).let {
            assertEquals(it.neighbors(), it.neighbors2())
        }
    }

    @Test
    fun part1CompareWithTestSolution() {
        val directions: List<Direction> = listOf(
            E, E, S, E, E, E, N, E, E, E, S, S, E, E, S, S, E, S, S, S, E, S, S, S, W, S, S, E
        )

        val heatData = mutableListOf(HeatData(0, listOf(Point(0, 0))))
        directions.forEach { d ->
            val h = heatData.last()
            val p = h.path.single().move(d)
            val v = testInput[p.y][p.x].digitToInt()
            heatData.add(HeatData(h.heatLoss + v, listOf(p)))
        }

        testInput.print { point, c ->
            if (heatData.map { it.path.single() }.contains(point)) {
                printColor(PrintColor.GREEN, c)
            } else {
                print(c)
            }
        }

        println()
        heatData.forEach {
            println("${it.path.single()}: ${it.heatLoss}")
        }
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val graph = input.map { line -> line.map { it.digitToInt() } }
        val start = Point(0, 0)
        val end = Point(graph.first().size - 1, graph.size - 1)
        val (minHeat, path) = findShortestPath(start, end, graph)

        input.print { point, c ->
            if (path.contains(point) || point == end) {
                printColor(PrintColor.GREEN, c)
            } else {
                print(c)
            }
        }

        return minHeat
    }

    private fun findShortestPath(start: Point, end: Point, boxes: List<List<Int>>): HeatData {
        val queue = mutableSetOf(SearchState(start, E, -1))
        val shortestPaths = mutableMapOf(start to HeatData(0, emptyList()))
        while (queue.isNotEmpty()) {
            val current = queue.minBy { boxes.heatLossOf(it.point) }
            queue.remove(current)

            if (current.point == end) return shortestPaths.getValue(end)

            val neighbors = current.neighbors().filter { it.point containedIn boxes }
            neighbors
                .filter { it.point !in shortestPaths }
                .forEach { neighbor ->
                    val (heatLossSoFar, path) = shortestPaths.getValue(current.point)
                    val minHeatLossToNeighbor = shortestPaths[neighbor.point]?.heatLoss ?: Int.MAX_VALUE
                    val nextHeatLoss = boxes.heatLossOf(neighbor.point)
                    if (heatLossSoFar + nextHeatLoss < minHeatLossToNeighbor) {
                        shortestPaths[neighbor.point] = HeatData(heatLossSoFar + nextHeatLoss, path + current.point)
                    }
                    queue.add(neighbor)
                }
        }

        throw IllegalArgumentException("Couldn't find path to $end")
    }

    data class HeatData(
        val heatLoss: Int,
        val path: List<Point>
    ) : Comparable<HeatData> {
        override fun compareTo(other: HeatData): Int = heatLoss.compareTo(other.heatLoss)
    }

    private fun List<List<Int>>.heatLossOf(point: Point): Int {
        return this[point.y][point.x]
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }

    private data class SearchState(
        val point: Point,
        val direction: Direction,
        val sameDirectionCount: Int = 0,
    ) {
        fun neighbors(): List<SearchState> {
            val leftRight = when (direction) {
                N, S -> listOf(SearchState(point.west(), W), SearchState(point.east(), E))
                E, W -> listOf(SearchState(point.north(), N), SearchState(point.south(), S))
            }
            return if (sameDirectionCount < 2) {

                leftRight + SearchState(point.move(direction), direction, sameDirectionCount + 1)
            } else {
                leftRight
            }
        }

        fun neighbors2(): List<SearchState> {
            val validDirections = listOf(N, S, W, E)
                .minus(direction.opposite)
                .run { if (sameDirectionCount >= 2) minus(direction) else this }

            return validDirections.map { direction ->
                val nextPoint = point.move(direction)
                val nextState = SearchState(
                    point = nextPoint,
                    direction = direction,
                    sameDirectionCount = if (this.direction == direction) sameDirectionCount.inc() else 0,
                )
                nextState
            }
        }
    }
}

private fun Point.move(direction: Direction): Point {
    return when (direction) {
        N -> this.north()
        E -> this.east()
        S -> this.south()
        W -> this.west()
    }
}
