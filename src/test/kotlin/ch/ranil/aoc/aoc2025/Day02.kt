package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day02 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(1227775554, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(18952700150, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(-1, compute2(testInput))
        assertEquals(-1, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return input
            .parsePairs()
            .flatMap { (l, r) -> (l..r).filter { it.isInvalid() } }
            .sum()
    }

    private fun compute2(input: List<String>): Long {
        return 0
    }

    private fun List<String>.parsePairs(): List<Pair<Long, Long>> {
        return this
            .joinToString(separator = "")
            .let { line -> line.split(",").map { pairString -> pairString.split("-") } }
            .map { (a, b) -> a.toLong() to b.toLong() }
    }

    private fun Long.isInvalid(): Boolean {
        val s = this.toString()
        val len = s.length
        if (len % 2 != 0) return false

        val l = s.substring(0..<(len / 2))
        val r = s.substring(len / 2)
        return l == r
    }
}