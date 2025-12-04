package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.charForOrNull
import ch.ranil.aoc.common.forEachPointWithChar
import ch.ranil.aoc.common.print
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day04 : AbstractDay() {

    @Test
    fun part1() {
        Debug.enable()
        assertEquals(13, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1384, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        Debug.enable()
        assertEquals(43, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(8013, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val map = Map(input)
        return map.removeRolls()
    }

    private fun compute2(input: List<String>): Long {
        var result = 0L
        val map = Map(input)
        do {
            val removed = map.removeRolls()
            result += removed
        } while (removed > 0)
        return result
    }

    private class Map(input: List<String>) : AbstractMap(input) {
        private val paperRolls = mutableSetOf<Point>()

        init {
            this.forEach { point, char ->
                if (char == '@') paperRolls.add(point)
            }
        }

        fun removeRolls(): Long {
            val removableRolls = mutableSetOf<Point>()
            paperRolls.forEach { point ->
                val paper = point
                    .edges()
                    .filter { e -> paperRolls.contains(e) }
                if (paper.size < 4) removableRolls.add(point)
            }

            Debug.debug {
                printMap { p, c ->
                    if (removableRolls.contains(p)) {
                        printColor("x", PrintColor.YELLOW)
                    } else {
                        val c = if (paperRolls.contains(p)) '@' else '.'
                        print(c)
                    }
                }
            }

            paperRolls.removeAll(removableRolls)
            return removableRolls.size.toLong()
        }
    }
}