package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day11 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(8, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(6701, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(4, compute2(test2Input))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return 0
    }


    private fun compute2(input: List<String>): Int {
        return input.count()
    }

}
