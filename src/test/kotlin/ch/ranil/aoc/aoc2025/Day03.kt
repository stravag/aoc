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
        return input.sumOf { it.maxNum1() }
    }

    private fun String.maxNum1(): Long {
        var maxNum = 0
        for (l in 0 until length - 1) {
            for (r in l + 1 until length) {
                maxNum = maxOf(maxNum, numFor(l, r))
            }
        }
        Debug.debug { println("$this >> $maxNum") }
        return maxNum.toLong()
    }

    private fun String.numFor(idxL: Int, idxR: Int) = get(idxL).digitToInt() * 10 + get(idxR).digitToInt()

    private fun compute2(input: List<String>): Long {
        return input.sumOf { it.maxNum2(12) }
    }

    private fun String.maxNum2(n: Int): Long {
        var leftPointer = 0
        var numString = ""
        (0 until n).forEach { i ->
            val candidates = substring(leftPointer, length - n + i + 1)
            val char = candidates.toList().max()
            numString += char
            leftPointer = indexOf(char, leftPointer) + 1
        }

        return numString.toLong()
    }
}