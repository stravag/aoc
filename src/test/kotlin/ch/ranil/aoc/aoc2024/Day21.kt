package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day21 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(0, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return input.size.toLong()
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }
}
