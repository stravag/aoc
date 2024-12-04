package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.MovePointBySteps
import ch.ranil.aoc.Point
import ch.ranil.aoc.charForPoint
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day04 : AbstractDay() {

    @Test
    fun testSearchXmas() {
        val input = """
            ..X...
            .SAMX.
            .A..A.
            XMAS.S
        """.trimIndent().lines()
        assertEquals(1, searchXmas(Point(2, 0), Point::southEast, input))
    }

    @Test
    fun part1Test() {
        assertEquals(18, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(2397, compute1(puzzleInput))
    }

    @Test
    fun testSearchXmasCross() {
        val input = """
            M.S
            .A.
            M.S
        """.trimIndent().lines()
        assertEquals(1, searchXmasCross(Point(1, 1), input))
    }

    @Test
    fun part2Test() {
        assertEquals(9, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1824, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        var xmasCount = 0L
        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                if (char == 'X') {
                    xmasCount += Point.directions.sumOf { searchXmas(Point(x, y), it, input) }
                }
            }
        }
        return xmasCount
    }

    private fun searchXmas(point: Point, move: MovePointBySteps, input: List<String>): Long {
        val crossWord = listOf(
            input.charForPoint(point),
            input.charForPoint(move(point, 1)),
            input.charForPoint(move(point, 2)),
            input.charForPoint(move(point, 3)),
        ).joinToString("")
        return if (crossWord == "XMAS") 1 else 0
    }

    private fun compute2(input: List<String>): Long {
        var xmasCount = 0L
        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                if (char == 'A') {
                    xmasCount += searchXmasCross(Point(x, y), input)
                }
            }
        }
        return xmasCount
    }

    private fun searchXmasCross(point: Point, input: List<String>): Long {
        val diag1 = listOf(
            input.charForPoint(point.northWest()),
            input.charForPoint(point),
            input.charForPoint(point.southEast()),
        ).joinToString("")

        val diag2 = listOf(
            input.charForPoint(point.southWest()),
            input.charForPoint(point),
            input.charForPoint(point.northEast()),
        ).joinToString("")

        val isDiag1 = (diag1 == "MAS" || diag1 == "SAM")
        val isDiag2 = (diag2 == "MAS" || diag2 == "SAM")

        return if (isDiag1 && isDiag2) 1 else 0
    }
}
