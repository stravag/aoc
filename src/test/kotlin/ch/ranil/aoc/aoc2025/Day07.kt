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
        assertEquals(40, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(73007003089792, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val splitterMap = SplitterMap(input)
        return splitterMap.calculateSplits()
    }

    private fun compute2(input: List<String>): Long {
        val splitterMap = SplitterMap(input)
        return splitterMap.countTimelines()
    }

    private class SplitterMap(input: List<String>) : AbstractMap(input) {
        val start = Point(row = 0, col = input.first().indexOf('S'))

        fun calculateSplits(): Int {
            var beams = setOf(start.south())
            var splitCount = 0
            while (beams.isNotEmpty()) {
                val beamsOfNextStep = mutableSetOf<Point>()
                beams.forEach { beam ->
                    val nextBeams = nextBeams(beam)
                    if (nextBeams.size == 2) splitCount++
                    beamsOfNextStep.addAll(nextBeams)
                }
                beams = beamsOfNextStep
            }
            return splitCount
        }

        fun countTimelines(): Long {
            val memo = mutableMapOf<Point, Long>()
            val active = mutableSetOf<Point>()

            fun dfs(point: Point): Long {
                if (point in memo) return memo.getValue(point)

                val sum = nextBeams(point)
                    .ifEmpty { return 1L }
                    .sumOf { n -> dfs(n) }

                active.remove(point)
                memo[point] = sum
                return sum
            }

            return dfs(start)
        }

        private fun nextBeams(p: Point): List<Point> {
            return when (this.charForOrNull(p.south())) {
                '.' -> listOf(p.south())
                '^' -> {
                    listOf(
                        p.southEast(),
                        p.southWest(),
                    )
                }

                else -> emptyList()
            }
        }
    }
}
