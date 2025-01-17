package ch.ranil.aoc.aoc2022

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

object Day09 : AbstractDay() {

    @Test
    fun tests() {
        assertEquals(13, compute(testInput, 2))
        assertEquals(6212, compute(puzzleInput, 2))

        assertEquals(1, compute(testInput, 10))
        assertEquals(
            36, compute(
                """
            R 5
            U 8
            L 8
            D 3
            R 17
            D 10
            L 25
            U 20
        """.trimIndent().lines(), 10
            )
        )
        assertEquals(2522, compute(puzzleInput, 10))
    }

    private fun compute(input: List<String>, ropeSize: Int): Int {
        val rope = Rope(ropeSize)
        val visited = mutableSetOf(rope.tail)
        input
            .flatMap { it.parse() }
            .forEach { direction ->
                rope.move(direction)
                visited.add(rope.tail)
            }

        return visited.size
    }

    private class Rope(private val positions: List<Position>) {

        constructor(ropeSize: Int) : this(List(ropeSize) { Position(0, 0) })

        fun move(direction: String) {
            head.move(direction)
            for (i in 1 until positions.size) {
                positions[i].follow(positions[i - 1])
            }
        }

        private val head: Position get() = positions.first()
        val tail: Position get() = positions.last().copy()
    }

    private data class Position(var x: Int, var y: Int) {
        fun move(direction: String) {
            when (direction) {
                "R" -> x++
                "L" -> x--
                "U" -> y++
                "D" -> y--
            }
        }

        fun follow(other: Position) {
            val xDiff = other.x - x
            val yDiff = other.y - y
            if (xDiff * xDiff + yDiff * yDiff > 2) {
                x += sign(xDiff)
                y += sign(yDiff)
            }
        }
    }

    private fun String.parse(): List<String> {
        val (direction, cnt) = split(" ")
        return List(cnt.toInt()) { direction }
    }
}
