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
        assertEquals(-1, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(-1, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return input.sumOf { it.maxNum() }
    }

    private fun String.maxNum(): Long {
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
        return 0
    }
}