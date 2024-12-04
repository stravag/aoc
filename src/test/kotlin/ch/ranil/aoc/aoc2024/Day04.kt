package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.*
import ch.ranil.aoc.aoc2024.Day04.Direction.*
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
        assertEquals(1, searchXmas(Point(2, 0), SE, input))
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
                    xmasCount += countXmas(Point(x, y), input)
                }
            }
        }
        return xmasCount
    }

    private fun countXmas(charLoc: Point, input: List<String>): Long {
        val result = Direction
            .entries
            .map { direction ->
                direction to searchXmas(charLoc, direction, input)
            }

        return result.sumOf { it.second }
    }

    private fun searchXmas(point: Point, direction: Direction, input: List<String>): Long {
        var nextPoint: Point = point
        for (i in "XMAS".indices) {
            val desiredChar = "XMAS"[i]
            val char = input.charForPoint(nextPoint)
            if (char == desiredChar) {
                nextPoint = nextPoint.move(1, direction)
            } else {
                return 0
            }
        }
        return 1
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
        val diag1 =
            "${input.charForPoint(point.move(1, NW)) ?: '.'}A${input.charForPoint(point.move(1, SE)) ?: '.'}"
        val diag2 =
            "${input.charForPoint(point.move(1, SW)) ?: '.'}A${input.charForPoint(point.move(1, NE)) ?: '.'}"

        val isDiag1 = (diag1 == "MAS" || diag1 == "SAM")
        val isDiag2 = (diag2 == "MAS" || diag2 == "SAM")

        return if (isDiag1 && isDiag2) 1 else 0
    }


    private enum class Direction {
        N, E, S, W, NE, NW, SE, SW;

        val opposite
            get() = when (this) {
                N -> S
                E -> W
                S -> N
                W -> E
                NE -> SW
                NW -> SE
                SE -> NW
                SW -> SE
            }
    }

    private fun Point.move(steps: Int = 1, direction: Direction): Point {
        return when (direction) {
            N -> copy(y = y - steps)
            E -> copy(x = x + steps)
            S -> copy(y = y + steps)
            W -> copy(x = x - steps)
            NE -> move(steps, N).move(steps, E)
            NW -> move(steps, N).move(steps, W)
            SE -> move(steps, S).move(steps, E)
            SW -> move(steps, S).move(steps, W)
        }
    }
}
