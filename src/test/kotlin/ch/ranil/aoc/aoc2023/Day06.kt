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
        assertEquals(71503, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(40651271, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val results = input
            .parse()
            .map { (time, distance) -> countPossibleOptions(time.toLong(), distance.toLong()).toInt() }
        return results.product()
    }

    private fun countPossibleOptions(time: Long, distance: Long): Long {
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
        return upperBoundary - lowerBoundary - 1
    }

    private fun compute2(input: List<String>): Long {
        val (time, distance) = input.parse2()
        return countPossibleOptions(time, distance)
    }

    private fun List<String>.parse(): List<Pair<Int, Int>> {
        val times = this[0].split("\\s+".toRegex()).mapNotNull { it.trim().toIntOrNull() }
        val dists = this[1].split("\\s+".toRegex()).mapNotNull { it.trim().toIntOrNull() }
        return times.zip(dists)
    }

    private fun List<String>.parse2(): Pair<Long, Long> {
        val time = this[0].replace("[^0-9]".toRegex(), "").toLong()
        val distance = this[1].replace("[^0-9]".toRegex(), "").toLong()
        return time to distance
    }
}
