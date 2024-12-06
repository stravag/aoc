package ch.ranil.aoc

import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

typealias MovePointBySteps = (Point, Int) -> Point

data class Point(override val x: Int, override val y: Int) : Coordinate {
    override fun toString(): String = "($x,$y)"

    fun north(step: Int = 1) = Point(x, y - step)
    fun east(step: Int = 1) = Point(x + step, y)
    fun south(step: Int = 1) = Point(x, y + step)
    fun west(step: Int = 1) = Point(x - step, y)
    fun northWest(step: Int = 1) = Point(x - step, y - step)
    fun northEast(step: Int = 1) = Point(x + step, y - step)
    fun southWest(step: Int = 1) = Point(x - step, y + step)
    fun southEast(step: Int = 1) = Point(x + step, y + step)

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

    fun move(direction: Direction, steps: Int = 1): Point {
        return when (direction) {
            Direction.N -> copy(y = y - steps)
            Direction.E -> copy(x = x + steps)
            Direction.S -> copy(y = y + steps)
            Direction.W -> copy(x = x - steps)
        }
    }

    companion object {
        val directions: List<MovePointBySteps> = listOf(
            Point::north,
            Point::east,
            Point::south,
            Point::west,
            Point::northWest,
            Point::northEast,
            Point::southWest,
            Point::southEast,
        )
    }
}

enum class Direction(val indicator: Char) {
    N('^'), E('>'), S('v'), W('<');

    val opposite
        get() = when (this) {
            N -> S
            E -> W
            S -> N
            W -> E
        }

    fun turn90(): Direction = when (this) {
        N -> E
        E -> S
        S -> W
        W -> N
    }
}

class PointTest {
    @Test
    fun testEdges() {
        assertEquals(8, Point(0, 0).edges().size)
    }

    @Test
    fun testDirections() {
        val point = Point(0, 0)
        val points = listOf(
            point.north(),
            point.east(),
            point.south(),
            point.west(),
            point.northWest(),
            point.northEast(),
            point.southWest(),
            point.southEast(),
        )

        assertEquals(point.edges().size, points.size)
        assertTrue { point.edges().all { points.contains(it) } }
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

    @Test
    fun boardContainsPoint() {
        val board = """
            ..
            ..
        """.trimIndent().lines()
        assertTrue(board.containsPoint(Point(0, 0)))
        assertTrue(board.containsPoint(Point(0, 1)))
        assertTrue(board.containsPoint(Point(1, 0)))
        assertTrue(board.containsPoint(Point(1, 0)))

        assertFalse(board.containsPoint(Point(-1, 0)))
        assertFalse(board.containsPoint(Point(0, -1)))
        assertFalse(board.containsPoint(Point(2, 0)))
        assertFalse(board.containsPoint(Point(0, 2)))
    }
}
