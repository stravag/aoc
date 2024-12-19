package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Direction.*
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals

class Day18 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(62, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(36679, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(952408144115, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(88007104020978, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        // shoelace methode
        var s1 = 0
        var s2 = 0
        var edgeLength = 0
        val points = mutableListOf(Point(0, 0))
        input
            .map { it.parse1() }
            .forEach { (direction, length) ->
                val lastPoint = points.last()
                val nextPoint = lastPoint.move(direction, length)

                s1 += (lastPoint.col * nextPoint.row)
                s2 += (lastPoint.row * nextPoint.col)
                edgeLength += length
                points.add(nextPoint)
            }

        val shoelaceArea = abs(s1 - s2) / 2
        return shoelaceArea + (edgeLength / 2) + 1
    }

    private fun String.parse1(): DigInstruction {
        val (d, l, _) = this.split(" ")
        return DigInstruction(
            direction = d.toDirection(),
            length = l.toInt(),
        )
    }

    private fun compute2(input: List<String>): Long {
        // shoelace methode
        var s = 0L
        val points = mutableListOf(Point(0, 0))
        input
            .map { it.parse2() }
            .forEach { (direction, length) ->
                val lastPoint = points.last()
                val nextPoint = lastPoint.move(direction, length)

                // simplified "shoelace" compared to part1
                s += (lastPoint.row.toLong() + nextPoint.row.toLong()) * (lastPoint.col.toLong() - nextPoint.col.toLong())
                s += length

                points.add(nextPoint)
            }

        return abs(s) / 2L + 1L
    }

    private fun String.parse2(): DigInstruction {
        val (_, lengthHex, dirStr) = Regex(".*\\(#(\\w{5})(\\w)\\)").find(this)?.groupValues.orEmpty()
        val length = lengthHex.toInt(16)
        val direction = when (dirStr) {
            "0" -> E
            "1" -> S
            "2" -> W
            "3" -> N
            else -> throw IllegalArgumentException(dirStr)
        }
        return DigInstruction(direction, length)
    }

    private data class DigInstruction(
        val direction: Direction,
        val length: Int,
    )

    private fun String.toDirection(): Direction {
        return when (this) {
            "R" -> E
            "L" -> W
            "U" -> N
            "D" -> S
            else -> throw IllegalArgumentException("Unknown direction: $this")
        }
    }
}
