package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day02 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(1227775554, compute1(testInput))
        assertEquals(-1, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(-1, compute2(testInput))
        assertEquals(-1, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return 0
    }

    private fun compute2(input: List<String>): Long {
        return 0
    }

}