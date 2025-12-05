package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day05 : AbstractDay() {

    @Test
    fun part1() {
        Debug.enable()
        assertEquals(3, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(798, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        Debug.enable()
        assertEquals(-1, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(-1, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val ranges = input
            .takeWhile { it.isNotBlank() }
            .map { row -> row.split("-").map { it.toLong() } }

        return input
            .dropWhile { it.isNotEmpty() }
            .drop(1)
            .map { it.toLong() }
            .count {
                ranges.contains(it)
            }
    }

    private fun List<List<Long>>.contains(value: Long): Boolean {
        return any { (l, r) ->
            l <= value && value <= r
        }
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }
}