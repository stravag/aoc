package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.*
import ch.ranil.aoc.PrintColor.GREEN
import ch.ranil.aoc.PrintColor.RED
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals

class Day08 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(14, compute1(testInput))
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

    private fun compute1(input: List<String>): Long {
        val knownAntennas = mutableMapOf<Char, List<Point>>()
        val uniqueAntiNodes = mutableSetOf<Point>()
        input.forEachPointWithChar { antenna, frequency ->
            if (frequency != '.') {
                val matchingAntennas = knownAntennas[frequency].orEmpty()
                matchingAntennas.forEach { matchingAntenna ->
                    val antiNodes = getAntiNodes(antenna, matchingAntenna)
                        .filter { input.containsPoint(it) }
                    uniqueAntiNodes.addAll(antiNodes)
                }
                knownAntennas.compute(frequency) { _, acc -> acc.orEmpty() + antenna }
            }
        }

        input.print { point, c ->
            when {
                uniqueAntiNodes.contains(point) -> printColor(RED, '#')
                c == '.' -> print(c)
                else -> printColor(GREEN, c)
            }
        }

        return uniqueAntiNodes.size.toLong()
    }

    private fun getAntiNodes(p1: Point, p2: Point): List<Point> {
        val dX = p1.x - p2.x
        val dY = p1.y - p2.y
        val antiNodes = listOf(
            Point(p1.x + dX, p1.y + dY),
            Point(p1.x - dX, p1.y - dY),
            Point(p2.x + dX, p2.y + dY),
            Point(p2.x - dX, p2.y - dY),
        )
        val antiNodesMinusAntennas = antiNodes.filter { it != p1 && it != p2 }
        return antiNodesMinusAntennas
    }

    private fun compute2(input: List<String>): Long {
        TODO()
    }
}
