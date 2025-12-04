package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.charFor
import ch.ranil.aoc.common.charForOrNull
import ch.ranil.aoc.common.containsPoint
import ch.ranil.aoc.common.forEachPointWithChar
import ch.ranil.aoc.common.print
import ch.ranil.aoc.common.printColor
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
        assertEquals(-1, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(-1, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val paperPoints = mutableSetOf<Point>()
        input.forEachPointWithChar { point, char ->
            if (char == '@') {
                val paper = point
                    .edges()
                    .filter { e -> input.charForOrNull(e) == '@' }
                if (paper.size < 4) paperPoints.add(point)
            }
        }

        Debug.debug {
            input.print { p, c ->
                if (paperPoints.contains(p)) {
                    printColor("x", PrintColor.YELLOW)
                } else {
                    print(c)
                }
            }
        }

        return paperPoints.size.toLong()
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

}