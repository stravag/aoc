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
        assertEquals(398, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(34, compute2(testInput))
    }

    @Test
    fun part2Test2() {
        val dummyInput = """
            T.........
            ...T......
            .T........
            ..........
            ..........
            ..........
            ..........
            ..........
            ..........
            ..........
        """.trimIndent().lines()
        assertEquals(9, compute2(dummyInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1333, compute2(puzzleInput))
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

    private fun compute2(input: List<String>): Long {
        val knownAntennas = mutableMapOf<Char, List<Point>>()
        val uniqueAntiNodes = mutableSetOf<Point>()
        input.forEachPointWithChar { antenna, frequency ->
            if (frequency != '.') {
                val matchingAntennas = knownAntennas[frequency].orEmpty()
                matchingAntennas.forEach { matchingAntenna ->
                    val antiNodes = getAntiNodes2(antenna, matchingAntenna, input)
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

    private fun getAntiNodes(p1: Point, p2: Point): Set<Point> {
        val dX = p1.x - p2.x
        val dY = p1.y - p2.y
        val antiNodes = setOf(
            Point(p1.x + dX, p1.y + dY),
            Point(p1.x - dX, p1.y - dY),
            Point(p2.x + dX, p2.y + dY),
            Point(p2.x - dX, p2.y - dY),
        )
        val antiNodesMinusAntennas = antiNodes.minus(p1).minus(p2)
        return antiNodesMinusAntennas
    }

    private fun getAntiNodes2(p1: Point, p2: Point, input: List<String>): Set<Point> {
        val dX = p1.x - p2.x
        val dY = p1.y - p2.y

        val antiNodes1 = walkInLine(
            point = p1,
            step = { Point(it.x + dX, it.y + dY) },
            input = input
        )

        val antiNodes2 = walkInLine(
            point = p1,
            step = { Point(it.x - dX, it.y - dY) },
            input = input
        )

        return antiNodes1 + antiNodes2 + p1
    }

    private fun walkInLine(point: Point, step: (Point) -> Point, input: List<String>): Set<Point> {
        var p = step(point)
        val pointsInLine = mutableSetOf<Point>()
        while (input.containsPoint(p)) {
            pointsInLine.add(p)
            p = step(p)
        }
        return pointsInLine
    }
}
