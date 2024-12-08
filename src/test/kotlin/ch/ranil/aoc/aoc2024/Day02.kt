package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day02 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(2, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(224, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(4, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(293, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val result = input
            .map { row -> row.split(" ").map { it.toInt() } }
            .map { row -> checkSafety(row) }

        return result.count { it }
    }

    private fun compute2(input: List<String>): Int {
        val result = input
            .map { row -> row.split(" ").map { it.toInt() } }
            .map { row -> checkSafetyWithTolerance(row) }

        return result.count { it }
    }

    private fun checkSafety(row: List<Int>): Boolean {
        val (increasing, decreasing) = areIncreasingDecreasing(row)

        return increasing.all { it } || decreasing.all { it }
    }

    private fun checkSafetyWithTolerance(row: List<Int>): Boolean {
        val (increasing, decreasing) = areIncreasingDecreasing(row)

        if (increasing.all { it } || decreasing.all { it }) return true

        if (increasing.count { !it } <= 2) {
            for (indexToRemove in row.indices) {
                val rowCandidate = row.filterIndexed { index, _ -> indexToRemove != index }
                val safe = checkSafety(rowCandidate)
                if (safe) return true
            }
        }

        if (decreasing.count { !it } <= 2) {
            for (indexToRemove in row.indices) {
                val rowCandidate = row.filterIndexed { index, _ -> indexToRemove != index }
                val safe = checkSafety(rowCandidate)
                if (safe) return true
            }
        }

        return false
    }

    private fun areIncreasingDecreasing(row: List<Int>): Pair<List<Boolean>, List<Boolean>> {
        val increasing = row.zipWithNext { l, r -> isIncreasing(l, r) }
        val decreasing = row.zipWithNext { l, r -> isDecreasing(l, r) }
        return increasing to decreasing
    }

    private fun isIncreasing(l: Int, r: Int) = (r - l) < 4 && (r - l) > 0

    private fun isDecreasing(l: Int, r: Int) = (l - r) < 4 && (l - r) > 0
}
