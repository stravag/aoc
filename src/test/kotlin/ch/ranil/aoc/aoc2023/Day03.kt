package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(0, compute1(testInput))
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
    fun part2() {
        assertEquals(0, compute2(testInput))
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {

        return input.size
    }

    private fun findNumberForSymbol(input: List<String>, point: Point) {
    }

    private fun searchNumberInStringAtPos(string: String, idx: Int, direction: Direction): Int? {
        fun find(range: IntProgression, combination: (String, Char) -> String): Int? {
            var numberStr: String? = null
            for (x in range) {
                val char = string[x]
                if (char.isDigit()) {
                    numberStr = combination(numberStr.orEmpty(), char)
                } else {
                    break
                }
            }
            return numberStr?.toInt()
        }

        return when (direction) {
            Direction.LEFT -> find((0..<idx).reversed()) { s, c -> "$c$s" }
            Direction.RIGHT -> find(idx + 1..<string.length) { s, c -> "$s$c" }
            Direction.BOTH -> {
                val left = find((0..idx).reversed()) { s, c -> "$c$s" }
                if (left != null) {
                    val right = find(idx + 1..<string.length) { s, c -> "$s$c" }
                    "$left${right ?: ""}".toIntOrNull()
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