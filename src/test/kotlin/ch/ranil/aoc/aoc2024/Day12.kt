package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.Debug.debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

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
        val start = garden.allPoints().first { garden.charFor(it) == 'R' }
        garden.getRegion(start)
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1377008, compute1(puzzleInput))
    }

    @Test
    fun part2TestDummy() {
        Debug.enable()

        assertEquals(
            80, compute2(
                """
                AAAA
                BBCD
                BBCC
                EEEC
            """.trimIndent().lines()
            )
        )

        assertEquals(
            236, compute2(
                """
                EEEEE
                EXXXX
                EEEEE
                EXXXX
                EEEEE
            """.trimIndent().lines()
            )
        )

        assertEquals(
            436, compute2(
                """
                OOOOO
                OXOXO
                OOOOO
                OXOXO
                OOOOO
            """.trimIndent().lines()
            )
        )


        assertEquals(
            368, compute2(
                """
                AAAAAA
                AAABBA
                AAABBA
                ABBAAA
                ABBAAA
                AAAAAA
            """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(1206, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(815788, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return Garden(input).calculateCost { it.areaSize * it.perimeter1 }
    }

    private fun compute2(input: List<String>): Long {
        return Garden(input).calculateCost { it.areaSize * it.perimeter2 }
    }

    private class Garden(input: List<String>) : AbstractMap(input) {

        fun calculateCost(costOf: (Region) -> Long): Long {
            val seenPlots = HashSet<Point>()
            var cost = 0L
            forEach { plot, _ ->
                if (plot !in seenPlots) {
                    val region = getRegion(plot)
                    cost += costOf(region)
                    seenPlots.addAll(region.areaPoints)
                    debug { printGarden(region) }
                }
            }

            return cost
        }

        fun getRegion(startPlot: Point): Region {
            val region = Region(charFor(startPlot))
            val seen = HashSet<Point>()
            val queue = LinkedList<Point>()
            queue.addLast(startPlot)
            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                if (current in seen) continue

                seen.add(current)
                region.addToArea(current)

                val next = current.directEdges()
                for (n in next) {
                    val c = charForOrNull(n)
                    if (region.crop == c) {
                        queue.add(n)
                    }
                    if (region.crop != c) {
                        region.addToPerimeter(pointInRegion = current, perimeterPoint = n)
                    }
                }
            }
            return region
        }

        fun printGarden(region: Region) {
            region.print()
            printMap { p, c ->
                when {
                    p in region -> printColor(c, PrintColor.GREEN)
                    else -> print(c)
                }
            }
        }
    }

    private class Region(
        val crop: Char,
        private val perimeter: MutableSet<Pair<Point, Point>> = HashSet(),
        private val area: MutableSet<Point> = HashSet(),
        private var topLeft: Point = Point(Int.MAX_VALUE, Int.MAX_VALUE),
        private var bottomRight: Point = Point(Int.MIN_VALUE, Int.MIN_VALUE),
    ) {
        operator fun contains(point: Point): Boolean = point in area

        val areaSize: Long
            get() = area.size.toLong()

        val areaPoints: Set<Point>
            get() = area

        val perimeter1: Long
            get() = perimeter.size.toLong()

        val perimeter2: Int
            get() {
                var corners = 0
                for (p in area) {
                    val northEast = listOf(p.north(), p.east())
                    if ((northEast.all { it in area } && p.northEast() !in area) || northEast.none { it in area }) {
                        corners++
                    }
                    val southWest = listOf(p.south(), p.west())
                    if ((southWest.all { it in area } && p.southWest() !in area) || southWest.none { it in area }) {
                        corners++
                    }
                    val southEast = listOf(p.south(), p.east())
                    if ((southEast.all { it in area } && p.southEast() !in area) || southEast.none { it in area }) {
                        corners++
                    }
                    val northWest = listOf(p.north(), p.west())
                    if ((northWest.all { it in area } && p.northWest() !in area) || northWest.none { it in area }) {
                        corners++
                    }
                }

                return corners
            }

        fun addToArea(point: Point) {
            area.add(point)
            topLeft = minOf(point, topLeft)
            bottomRight = maxOf(point, topLeft)
        }

        fun addToPerimeter(pointInRegion: Point, perimeterPoint: Point) {
            perimeter.add(pointInRegion to perimeterPoint)
        }

        fun print() {
            println("Region $crop")
            println("Perimeter1: $perimeter1")
            println("Perimeter2: $perimeter2")
        }
    }
}
