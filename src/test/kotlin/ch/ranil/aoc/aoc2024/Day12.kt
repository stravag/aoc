package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.Debug.debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import java.util.LinkedList
import kotlin.test.assertEquals
import kotlin.time.measureTime

class Day12 : AbstractDay() {

    @Test
    fun part1TestDummy() {
        Debug.enable()
        assertEquals(
            28, compute1(
                """
            AA
            BA
        """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(1930, compute1(testInput))
    }

    @Test
    fun part1Test2() {
        Debug.enable()

        val garden = Garden(test2Input)
        garden.getRegion(Point(16, 1))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
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

    private fun compute1(input: List<String>): Long {
        return Garden(input).calculateCost()
    }

    private fun compute2(input: List<String>): Long {
        TODO()
    }

    private class Garden(input: List<String>) : AbstractMap(input) {

        fun calculateCost(): Long {
            val seenPlots = HashSet<Point>()
            var cost = 0L
            forEach { plot, _ ->
                if (plot !in seenPlots) {
                    val region = getRegion(plot)
                    cost += region.cost()
                    seenPlots.addAll(region.area)
                    debug { printGarden(region) }
                }
            }

            return cost
        }

        fun getRegion(startPlot: Point): Region {
            val crop = charFor(startPlot)
            val region = Region(crop)
            println("Calculating Region $crop $startPlot")
            val duration = measureTime {
                var i = 0
                val seen = HashSet<Point>()
                val queue = LinkedList<Point>()
                queue.addLast(startPlot)
                while (queue.isNotEmpty()) {
                    val current = queue.removeFirst()

                    debug {
                        if (i > 1000) printGarden(seen, current)
                    }

                    seen.add(current)
                    region.area.add(current)

                    val next = current.directEdges()
                    for (n in next) {
                        if (n !in seen) {
                            val c = charForOrNull(n)
                            if (crop == c) {
                                queue.add(n)
                            }
                            if (crop != c) {
                                region.perimeter.add(current to n)
                            }
                        }
                    }
                    i++
                }
            }
            println("Calculated Region $crop $startPlot in $duration")
            return region
        }

        private fun printGarden(region: Region) {
            region.print()
            printMap { p, c ->
                when {
                    p in region.area -> printColor(c, PrintColor.GREEN)
                    else -> print(c)
                }
            }
        }

        private fun printGarden(seen: Set<Point>, current: Point) {
            printMap { p, c ->
                when (p) {
                    current -> printColor('$', PrintColor.RED)
                    in seen -> printColor('.', PrintColor.GREEN)
                    else -> print(c)
                }
            }
        }
    }

    private class Region(
        val crop: Char?,
        val perimeter: MutableSet<Pair<Point, Point>> = HashSet(),
        val area: MutableSet<Point> = HashSet(),
    ) {
        fun cost(): Long = area.size.toLong() * perimeter.size.toLong()

        fun print() {
            println("Region $crop: ${area.size} * ${perimeter.size} = ${cost()}")
        }
    }
}
