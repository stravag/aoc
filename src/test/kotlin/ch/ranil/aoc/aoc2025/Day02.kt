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
        assertEquals(4174379265, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(28858486244, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return input
            .parsePairs()
            .flatMap { (l, r) -> (l..r).filter { it.isInvalid1() } }
            .sum()
    }

    private fun compute2(input: List<String>): Long {
        return input
            .parsePairs()
            .flatMap { (l, r) -> (l..r).filter { it.isInvalid2() } }
            .sum()
    }

    private fun List<String>.parsePairs(): List<Pair<Long, Long>> {
        return this
            .joinToString(separator = "")
            .let { line -> line.split(",").map { pairString -> pairString.split("-") } }
            .map { (a, b) -> a.toLong() to b.toLong() }
    }

    private fun Long.isInvalid1(): Boolean {
        val s = this.toString()
        val len = s.length
        if (len % 2 != 0) return false

        val l = s.substring(0..<(len / 2))
        val r = s.substring(len / 2)
        return l == r
    }

    private fun Long.isInvalid2(): Boolean {
        val s = this.toString()

        for (len in 1..(s.length / 2)) {
            val chunks = s.chunked(len)
            if (chunks.distinct().size == 1) return true
        }

        return false
    }
}