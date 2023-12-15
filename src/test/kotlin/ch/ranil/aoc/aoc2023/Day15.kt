package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day15 : AbstractDay() {

    @Test
    fun part1TestHash() {
        assertEquals(52, hash("HASH"))
    }

    @Test
    fun part1Test() {
        assertEquals(516804, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(110565, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input
            .joinToString("")
            .split(",")
            .sumOf {
                hash(it)
            }
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }

    private fun hash(s: String): Int {
        return s.fold(0) { acc, c ->
            var newAcc = acc + c.code
            newAcc *= 17
            newAcc %= 256
            newAcc
        }
    }
}
