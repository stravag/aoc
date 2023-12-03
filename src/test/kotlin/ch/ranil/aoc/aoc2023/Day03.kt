package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(4361, compute1(testInput))
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part1Dummy() {
        assertEquals(null, searchNumberInStringAtPos(".123.*...", 5, Direction.LEFT))
        assertEquals(123, searchNumberInStringAtPos(".123*...", 4, Direction.LEFT))

        assertEquals(null, searchNumberInStringAtPos(".*.123..", 1, Direction.RIGHT))
        assertEquals(123, searchNumberInStringAtPos(".*123..", 1, Direction.RIGHT))

        assertEquals(null, searchNumberInStringAtPos(".123.", 0, Direction.BOTH))
        assertEquals(123, searchNumberInStringAtPos(".123.", 1, Direction.BOTH))
        assertEquals(123, searchNumberInStringAtPos(".123.", 2, Direction.BOTH))
        assertEquals(123, searchNumberInStringAtPos(".123.", 3, Direction.BOTH))
        assertEquals(null, searchNumberInStringAtPos(".123.", 4, Direction.BOTH))
    }

    @Test
    fun part1Dummy2() {
        val trimIndent = """
            ...../.
            ...*...
            535.848
        """.trimIndent().lines()
        assertEquals(535+848, compute1(trimIndent))
    }

    @Test
    fun part2() {
        assertEquals(0, compute2(testInput))
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val numbers = input
            .flatMapIndexed { y, line ->
                line.flatMapIndexed { x, c ->
                    getNumbersForPosition(c, input, Point(x, y))
                }
            }
        return numbers.sum()
    }

    private fun getNumbersForPosition(
        c: Char,
        input: List<String>,
        pos: Point,
    ) = when {
        c.isDigit() || c == '.' -> emptyList()
        else -> findNumberForSymbol(input, pos)
    }

    private fun findNumberForSymbol(input: List<String>, point: Point): List<Long> {
        val topRow = input.getOrNull(point.y - 1).orEmpty()
        val currRow = input.getOrNull(point.y).orEmpty()
        val bottomRow = input.getOrNull(point.y + 1).orEmpty()

        var topNums = listOfNotNull(searchNumberInStringAtPos(topRow, point.x, Direction.BOTH))
        if (topNums.isEmpty()) {
            topNums = listOfNotNull(
                searchNumberInStringAtPos(topRow, point.x, Direction.LEFT),
                searchNumberInStringAtPos(topRow, point.x, Direction.RIGHT),
            )
        }

        var bottomNums = listOfNotNull(searchNumberInStringAtPos(bottomRow, point.x, Direction.BOTH))
        if (bottomNums.isEmpty()) {
            bottomNums = listOfNotNull(
                searchNumberInStringAtPos(bottomRow, point.x, Direction.LEFT),
                searchNumberInStringAtPos(bottomRow, point.x, Direction.RIGHT),
            )
        }

        val inlineNums = listOfNotNull(
            // top row
            searchNumberInStringAtPos(currRow, point.x, Direction.LEFT),
            searchNumberInStringAtPos(currRow, point.x, Direction.RIGHT),
        )
        return topNums + inlineNums + bottomNums
    }

    private fun searchNumberInStringAtPos(string: String, idx: Int, direction: Direction): Long? {
        fun find(range: IntProgression, combination: (String, Char) -> String): Long? {
            var numberStr: String? = null
            for (x in range) {
                val char = string.getOrNull(x)
                if (char != null && char.isDigit()) {
                    numberStr = combination(numberStr.orEmpty(), char)
                } else {
                    break
                }
            }
            return numberStr?.toLong()
        }

        return when (direction) {
            Direction.LEFT -> find((0..<idx).reversed()) { s, c -> "$c$s" }
            Direction.RIGHT -> find(idx + 1..<string.length) { s, c -> "$s$c" }
            Direction.BOTH -> {
                val left = find((0..idx).reversed()) { s, c -> "$c$s" }
                if (left != null) {
                    val right = find(idx + 1..<string.length) { s, c -> "$s$c" }
                    "$left${right ?: ""}".toLongOrNull()
                } else {
                    null
                }
            }
        }
    }

    enum class Direction {
        LEFT, RIGHT, BOTH
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }
}