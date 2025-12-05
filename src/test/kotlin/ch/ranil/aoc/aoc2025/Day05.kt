package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
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
        assertEquals(14, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(366181852921027, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val ranges = input
            .takeWhile { it.isNotBlank() }
            .map { row -> row.split("-").map { it.toLong() } }
            .map { (l, r) -> l..r }

        return input
            .dropWhile { it.isNotEmpty() }
            .drop(1)
            .map { it.toLong() }
            .count { id ->
                ranges.any { id in it }
            }
    }

    private fun compute2(input: List<String>): Long {
        return input
            .takeWhile { it.isNotBlank() }
            .map { row -> row.split("-").map { it.toLong() } }
            .map { (l, r) -> l..r }
            .merge()
            .sumOf { it.last + 1 - it.first }
    }

    private fun List<LongRange>.merge(): List<LongRange> {
        if (isEmpty()) return emptyList()

        val sorted = this.sortedBy { it.start }
        val result = mutableListOf<LongRange>()

        var current = sorted.first()

        for (r in sorted.drop(1)) {
            if (r.start <= current.last + 1) {
                current = current.start..maxOf(current.last, r.last())
            } else {
                result.add(current)
                current = r
            }
        }
        result.add(current)

        return result
    }
}