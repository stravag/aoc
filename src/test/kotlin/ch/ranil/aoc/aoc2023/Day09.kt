package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day09 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(114, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1731106378, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(2, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1087, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input
            .sumOf { line ->
                line
                    .readNumbers()
                    .getHistory()
                    .extrapolateRight()
            }
    }

    private fun compute2(input: List<String>): Int {
        return input
            .sumOf { line ->
                line
                    .readNumbers()
                    .getHistory()
                    .extrapolateLeft()
            }
    }

    private fun String.readNumbers(): List<Int> {
        return this.split(" ").map { it.toInt() }
    }

    private fun List<List<Int>>.extrapolateRight(): Int {
        val numbers = this
        var i = numbers.size - 2
        var extrapolatedNum = 0
        while (i >= 0) {
            extrapolatedNum += numbers[i--].last()
            println("Extrapolated $extrapolatedNum")
        }
        return extrapolatedNum
    }

    private fun List<List<Int>>.extrapolateLeft(): Int {
        val numbers = this
        var i = numbers.size - 2
        var extrapolatedNum = 0
        while (i >= 0) {
            extrapolatedNum = numbers[i--].first() - extrapolatedNum
            println("Extrapolated $extrapolatedNum")
        }
        return extrapolatedNum
    }

    private fun List<Int>.getHistory(): List<List<Int>> {
        println("Building History for $this")
        val histories = mutableListOf(this)
        while (!histories.last().all { it == 0 }) {
            val nextLine = nextHistory(histories.last())
            println(nextLine)
            histories.add(nextLine)
        }
        return histories
    }

    private fun nextHistory(numbers: List<Int>): List<Int> {
        return numbers.windowed(2) { (a, b) ->
            b - a
        }
    }
}
