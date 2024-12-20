package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day20 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(44, compute1(minTimeSaved = 0, testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1323, compute1(minTimeSaved = 100, puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(minTimeSaved: Int, input: List<String>): Int {
        val maze = Maze(input)
        return maze.countCheats(minTimeSaved)
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private class Maze(input: List<String>) : AbstractMap(input) {
        private val startPoint: Point
        private val endPoint: Point

        private val cache: MutableMap<Point, Int> = mutableMapOf()

        fun countCheats(minTimeSaved: Int): Int {
            followPath()

            val cheats = cache.keys.flatMap { it.getCheats() }
            val timeSavedByShortcuts = cheats
                .mapNotNull { cheat ->
                    val timeSaved = cache.getValue(cheat.source) - cache.getValue(cheat.target) - cheat.time
                    if (timeSaved > 0) {
                        Debug.debug {
                            println("Cheat $cheat saved $timeSaved picoseconds")
                            printMaze(listOf(cheat))
                        }
                        timeSaved
                    } else null
                }

            return timeSavedByShortcuts.count { it >= minTimeSaved }
        }

        private fun followPath() {
            var distanceToEnd = 0
            var nextPoint: Point? = endPoint
            while (nextPoint != null) {
                cache[nextPoint] = distanceToEnd++
                nextPoint = nextPoint
                    .directEdges()
                    .singleOrNull { it.isPath() && it !in cache }
                Debug.debug { printMaze(emptySet()) }
            }
        }

        private fun Point.isPath(): Boolean = charForOrNull(this) !in listOf(null, '#')

        private fun Point.getCheats(): List<Cheat> {
            return this
                .directEdges()
                .filterNot { it.isPath() }
                .map { skippedWall ->
                    val (dRow, dCol) = this.diffTo(skippedWall)
                    Cheat(
                        source = this,
                        target = Point(skippedWall.row + dRow, skippedWall.col + dCol)
                    )
                }
                .filter { it.target.isPath() }
        }

        private fun printMaze(cheats: Collection<Cheat>) {
            val cheatsTo = cheats.map { it.target }
            printMap(border = false) { point, c ->
                when (c) {
                    '#' -> print(c)
                    '.' -> {
                        when (point) {
                            in cheatsTo -> printColor('O', PrintColor.RED)
                            in cache -> printColor('O', PrintColor.GREEN)
                            else -> print(c)
                        }
                    }

                    'S', 'E' -> {
                        when (point) {
                            in cheatsTo -> printColor(c, PrintColor.RED)
                            in cache -> printColor(c, PrintColor.GREEN)
                            else -> printColor(c, PrintColor.YELLOW)
                        }
                    }

                    else -> error("Unknown char: $c")
                }
            }
            println()
        }

        init {
            val (e, s) = allPoints()
                .filter { charFor(it) in listOf('E', 'S') }
                .map { it to charFor(it) }
                .sortedBy { it.second }
                .sortedBy { it.second }
                .map { it.first }

            startPoint = s
            endPoint = e
        }
    }

    private data class Cheat(val source: Point, val target: Point) {
        val time = source.distanceTo(target)
    }
}
