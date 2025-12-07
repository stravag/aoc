package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day07 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(21, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1581, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val splitterMap = SplitterMap(input)
        return splitterMap.calculateSplits()
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private class SplitterMap(input: List<String>) : AbstractMap(input) {
        val start = Point(row = 0, col = input.first().indexOf('S'))

        fun calculateSplits(): Int {
            var beams = setOf(start.south())
            var splitCount = 0
            while (beams.isNotEmpty()) {
                val beamsOfNextStep = mutableSetOf<Point>()
                beams.forEach { beam ->
                    val next = beam.south()
                    when (this.charForOrNull(next)) {
                        '.' -> beamsOfNextStep.add(next)
                        '^' -> {
                            splitCount++
                            beamsOfNextStep.add(next.east())
                            beamsOfNextStep.add(next.west())
                        }

                        else -> Unit
                    }
                }
                beams = beamsOfNextStep
            }
            return splitCount
        }
    }
}
