package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.math.abs
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
        assertEquals(285, compute2(minTimeSaved = 50, testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(983905, compute2(minTimeSaved = 100, puzzleInput))
    }

    private fun compute1(minTimeSaved: Int, input: List<String>): Int {
        val maze = Maze(input)
        return maze.countCheats1(minTimeSaved)
    }

    private fun compute2(minTimeSaved: Int, input: List<String>): Int {
        val maze = Maze(input)
        return maze.countCheats2(minTimeSaved)
    }

    private class Maze(input: List<String>) : AbstractMap(input) {
        private val startPoint: Point
        private val endPoint: Point

        private val cache: MutableMap<Point, Int> = mutableMapOf()

        fun countCheats1(minTimeSaved: Int): Int {
            return countCheats(minTimeSaved) { it.getCheats() }
        }

        fun countCheats2(minTimeSaved: Int): Int {
            return countCheats(minTimeSaved) { it.getCheats2() }
        }

        private fun countCheats(minTimeSaved: Int, cheatProvider: (Point) -> List<Cheat>): Int {
            followPath()

            val cheats = cache.keys.flatMap(cheatProvider)
            val timeSavedByShortcuts = cheats
                .mapNotNull { cheat ->
                    val timeSaved = cache.getValue(cheat.source) - cache.getValue(cheat.target) - cheat.time
                    if (timeSaved > 0) {
                        Debug.debug {
                            println("Cheat $cheat saved $timeSaved picoseconds")
                            printMaze(cheat)
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
            }
        }

        private fun Point.isPath(): Boolean = charForOrNull(this) !in listOf(null, '#')

        private fun Point.getCheats(): List<Cheat> {
            return listOf(
                north(2),
                east(2),
                south(2),
                west(2),
            ).filter { it.isPath() }.map { Cheat(source = this, target = it) }
        }

        private fun Point.getCheats2(): List<Cheat> {
            return ((-20)..20).flatMap { row ->
                val n = 20 - abs(row)
                ((-n)..n).map { col -> Point(this.row + row, this.col + col) }
            }.filter { it.isPath() }.map { Cheat(source = this, target = it) }
        }

        private fun printMaze(cheat: Cheat) {
            printMap(border = false) { point, c ->
                when (c) {
                    '#' -> print(c)
                    '.' -> {
                        when (point) {
                            cheat.target -> printColor('O', PrintColor.RED)
                            cheat.source -> printColor('O', PrintColor.BLUE)
                            in cache -> printColor('O', PrintColor.GREEN)
                            else -> print(c)
                        }
                    }

                    'S', 'E' -> {
                        when (point) {
                            cheat.target -> printColor(c, PrintColor.RED)
                            cheat.source -> printColor(c, PrintColor.BLUE)
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
