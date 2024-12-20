package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.*
import ch.ranil.aoc.common.PrintColor.GREEN
import ch.ranil.aoc.common.PrintColor.RED
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
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
        return Map(input).antiNodes1()
    }

    private fun compute2(input: List<String>): Long {
        return Map(input).antiNodes2()
    }

    private class Map(input: List<String>) : AbstractMap(input) {
        fun antiNodes1(): Long {
            return compute { p1, p2 ->
                val (dRow, dCol) = p1.diffTo(p2)
                setOf(
                    Point(p1.row + dRow, p1.col + dCol),
                    Point(p1.row - dRow, p1.col - dCol),
                    Point(p2.row + dRow, p2.col + dCol),
                    Point(p2.row - dRow, p2.col - dCol),
                ).minus(p1).minus(p2).filter(::isPointInMap)
            }
        }

        fun antiNodes2(): Long {
            return compute { p1, p2 ->
                val (dRow, dCol) = p1.diffTo(p2)
                val antiNodes1 = p1.walkInLine { Point(it.row + dRow, it.col + dCol) }
                val antiNodes2 = p1.walkInLine { Point(it.row - dRow, it.col - dCol) }
                antiNodes1 + antiNodes2 + p1
            }
        }

        private fun Point.walkInLine(step: (Point) -> Point): Set<Point> {
            var p = step(this)
            val pointsInLine = mutableSetOf<Point>()
            while (isPointInMap(p)) {
                pointsInLine.add(p)
                p = step(p)
            }
            return pointsInLine
        }

        private fun compute(antiNodes: (Point, Point) -> Collection<Point>): Long {
            val knownAntennas = mutableMapOf<Char, List<Point>>()
            val uniqueAntiNodes = mutableSetOf<Point>()

            allPoints()
                .filter { charFor(it) != '.' }
                .forEach { antenna ->
                    val frequency = charFor(antenna)
                    val nodes = knownAntennas[frequency]
                        .orEmpty()
                        .flatMap { antiNodes(antenna, it) }
                    uniqueAntiNodes.addAll(nodes)
                    knownAntennas.compute(frequency) { _, acc -> acc.orEmpty() + antenna }
                }

            printMap { point, c ->
                when {
                    uniqueAntiNodes.contains(point) -> printColor('#', RED)
                    c == '.' -> print(c)
                    else -> printColor(c, GREEN)
                }
            }

            return uniqueAntiNodes.size.toLong()
        }
    }
}
