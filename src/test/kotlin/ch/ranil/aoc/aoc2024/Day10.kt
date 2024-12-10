package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day10 : AbstractDay() {

    @Test
    fun part1Test() {
        debug = true
        assertEquals(36, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        debug = true
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val map = Map(input)
        return map.findTrails().toLong()
    }

    private fun compute2(input: List<String>): Long {
        TODO()
    }

    private class Map(input: List<String>) : AbstractMap(input) {
        private var knownPaths = mutableSetOf<Point>()

        fun findTrails(): Int {
            return allPoints()
                .filter { heightAt(it) == 0 }
                .sumOf { findTrails(it) }
        }

        private fun findTrails(trailhead: Point, seen: MutableSet<Point> = mutableSetOf()): Int {
            return 0
        }

        private fun heightAt(point: Point): Int {
            return this.charForOrNull(point)?.digitToIntOrNull() ?: -1
        }
    }
}
