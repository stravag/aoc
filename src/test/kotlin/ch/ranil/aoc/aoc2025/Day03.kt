package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03 : AbstractDay() {

    @Test
    fun part1() {
        Debug.enable()
        assertEquals(357, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(17196, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        Debug.enable()
        assertEquals(3121910778619, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(171039099596062, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return input.sumOf { it.maxNum(2) }
    }

    private fun compute2(input: List<String>): Long {
        return input.sumOf { it.maxNum(12) }
    }

    private fun String.maxNum(n: Int): Long {
        var leftPointer = 0
        var numString = ""
        (0 until n).forEach { i ->
            val char = substring(leftPointer, length - n + i + 1).max()
            numString += char
            leftPointer = indexOf(char, leftPointer) + 1
        }

        return numString.toLong()
    }
}