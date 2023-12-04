package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day05 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(0, compute1(testInput))
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(0, compute2(testInput))
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input.size
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }
}
