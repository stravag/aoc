package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(161, compute1(testInput))
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


    private fun compute1(input: List<String>): Int {
        return input
            .sumOf { it.parse() }
    }

    private val regex = "mul\\(([0-9]{1,3}),([0-9]{1,3})\\)".toRegex()
    private fun String.parse(): Int {
        return regex
            .findAll(this)
            .sumOf { matchResult ->
                val (_, i1, i2) = matchResult.groupValues
                i1.toInt() * i2.toInt()
            }
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }
}
