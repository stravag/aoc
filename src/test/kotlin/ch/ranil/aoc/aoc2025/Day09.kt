package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.math.absoluteValue
import kotlin.test.assertEquals

class Day09 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(50, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(4750297200, compute1(puzzleInput))
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

    private fun compute1(input: List<String>): Long {
        val points = input.map(::toPoint)
        val largestArea = points
            .uniquePairs()
            .maxOf { (a, b) ->
                Debug.debug {
                    print(points, a, b)
                }
                area(a, b)
            }

        return largestArea
    }

    private fun area(a: Point, b: Point): Long {
        val dx = (b.row - a.row)
        val dy = (b.col - a.col)
        val area = (dx.absoluteValue + 1).toLong() * (dy.absoluteValue + 1).toLong()
        return area
    }

    private fun toPoint(string: String): Point {
        val (col, row) = string.split(",").map { it.toInt() }
        return Point(row, col)
    }

    private fun print(points: List<Point>, p1: Point, p2: Point) {
        val rows = minOf(p1.row, p2.row)..maxOf(p1.row, p2.row)
        val cols = minOf(p1.col, p2.col)..maxOf(p1.col, p2.col)
        val minRow = points.minOf { it.row } - 1
        val minCol = points.minOf { it.col } - 2
        val maxRow = points.maxOf { it.row } + 1
        val maxCol = points.maxOf { it.col } + 2
        println("$p1 -> $p2 = ${area(p1, p2)}")
        for (row in minRow..maxRow) {
            for (col in minCol..maxCol) {
                val p = Point(row, col)
                when {
                    p == p1 || p == p2 -> printColor("O", PrintColor.RED)
                    p.row in rows && p.col in cols -> printColor("O", PrintColor.YELLOW)
                    p in points -> printColor("#", PrintColor.GREEN)
                    else -> print(".")
                }
            }
            println()
        }
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private fun List<Point>.uniquePairs(): List<List<Point>> {
        val result = mutableListOf<List<Point>>()
        for (i in indices) {
            for (j in i + 1 until size) {
                result.add(
                    listOf(this[i], this[j]).sorted()
                )
            }
        }
        return result
    }
}
