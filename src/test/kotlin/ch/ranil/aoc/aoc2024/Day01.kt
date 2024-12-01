package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals

class Day01 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(11, compute1(testInput))
        assertEquals(2815556, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(31, compute2(testInput))
        assertEquals(23927637, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val (l1, l2) = input.split()
        return l1.sorted()
            .zip(l2.sorted())
            .sumOf { abs(it.first - it.second) }
    }

    private fun compute2(input: List<String>): Int {
        val (l1, l2) = input.split()

        val occurrences = l2
            .groupingBy { it }
            .eachCount()

        val sums = l1.map { it * (occurrences[it] ?: 0) }
        return sums.sum()
    }

    private fun List<String>.split(): Pair<List<Int>, List<Int>> {
        val pairs = this
            .map { it.split(" +".toRegex()) }
            .map { it[0].toInt() to it[1].toInt() }

        val l1 = pairs.map { it.first }
        val l2 = pairs.map { it.second }
        return Pair(l1, l2)
    }
}