package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.product
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day06 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(288, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(5133600, compute1(puzzleInput))
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
        val results = input
            .parse()
            .map { (time, distance) ->
                val lowerBoundary = (1..time)
                    .takeWhile { holdButtonForMs ->
                        val d = holdButtonForMs * (time - holdButtonForMs)
                        d <= distance
                    }
                    .last()
                val upperBoundary = (1..time).reversed()
                    .takeWhile { holdButtonForMs ->
                        val d = holdButtonForMs * (time - holdButtonForMs)
                        d <= distance
                    }
                    .last()
                upperBoundary - lowerBoundary - 1
            }
        return results.product()
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    private fun List<String>.parse(): List<Pair<Int, Int>> {
        val times = this[0].split("\\s+".toRegex()).mapNotNull { it.trim().toIntOrNull() }
        val dists = this[1].split("\\s+".toRegex()).mapNotNull { it.trim().toIntOrNull() }
        return times.zip(dists)
    }
}
