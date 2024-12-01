package ch.ranil.aoc

import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface Coordinate : Comparable<Coordinate> {
    val x: Int
    val y: Int

    override fun compareTo(other: Coordinate): Int {
        return compareValuesBy(this, other, { it.y }, { it.x })
    }

    fun distanceTo(other: Coordinate): Int {
        return abs(other.x - x) + abs(other.y - y)
    }

    fun isAdjacentTo(other: Point): Boolean {
        return (abs(other.x - this.x) <= 1) and (abs(other.y - this.y) <= 1)
    }
}

data class Point(override val x: Int, override val y: Int) : Coordinate {
    override fun toString(): String = "($x,$y)"

    fun north() = Point(x, y - 1)
    fun east() = Point(x + 1, y)
    fun south() = Point(x, y + 1)
    fun west() = Point(x - 1, y)

    fun edges(): List<Point> {
        return listOf(
            // Above
            Point(x - 1, y - 1),
            Point(x, y - 1),
            Point(x + 1, y - 1),
            // Side
            Point(x - 1, y),
            Point(x + 1, y),
            // Below
            Point(x - 1, y + 1),
            Point(x, y + 1),
            Point(x + 1, y + 1),
        )
    }

    fun move(steps: Int, direction: Direction): Point {
        return when (direction) {
            Direction.N -> copy(y = y - steps)
            Direction.E -> copy(x = x + steps)
            Direction.S -> copy(y = y + steps)
            Direction.W -> copy(x = x - steps)
        }
    }
}

enum class Direction {
    N, E, S, W;

    val opposite
        get() = when (this) {
            N -> S
            E -> W
            S -> N
            W -> E
        }
}

class PointTest {
    @Test
    fun testEdges() {
        assertEquals(8, Point(0, 0).edges().size)
    }

    @Test
    fun testEdgesAndAdjacent() {
        val center = Point(0, 0)
        assertTrue(center.edges().all { edge -> edge.isAdjacentTo(center) })
    }

    @Test
    fun testDistance() {
        val zero = Point(0, 0)
        assertEquals(0, zero.distanceTo(zero))
        assertEquals(1, zero.distanceTo(Point(1, 0)))
        assertEquals(1, zero.distanceTo(Point(0, 1)))
        assertEquals(1, zero.distanceTo(Point(-1, 0)))
        assertEquals(1, zero.distanceTo(Point(0, -1)))
        assertEquals(2, zero.distanceTo(Point(1, 1)))
    }
}
