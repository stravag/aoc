package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day01 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(
            3, compute1(
                """
                L68
                L30
                R48
                L5
                R60
                L55
                L1
                L99
                R14
                L82
            """.trimIndent().lines()
            )
        )
        assertEquals(1135, compute1(puzzleInput))
    }

    @Test
    fun part2() {
    }

    private fun compute1(input: List<String>): Int {
        var count = 0
        var position = 50
        input.map {
            val operand = it.operand()
            val steps = it.drop(1).toInt()

            position = operand(position, steps) % 100
            if (position == 0) count++
        }
        return count
    }

    private fun String.operand(): (Int, Int) -> Int {
        val operand: (Int, Int) -> Int = when (this.first()) {
            'L' -> Int::minus
            'R' -> Int::plus
            else -> throw IllegalArgumentException(this)
        }
        return operand
    }
}