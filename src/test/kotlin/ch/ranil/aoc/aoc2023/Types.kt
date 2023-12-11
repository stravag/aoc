package ch.ranil.aoc.aoc2023

import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

data class Point(val x: Int, val y: Int)

fun Point.isAdjacentTo(other: Point): Boolean {
    return (abs(other.x - this.x) <= 1) and (abs(other.y - this.y) <= 1)
}

fun Point.edges(): List<Point> {
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

fun Point.distanceTo(other: Point): Int {
    return abs(other.x - x) + abs(other.y - y)
}

class PointTest {
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
    fun testEdgesUnique() {
        assertEquals(8, Point(0, 0).edges().distinct().size)
    }
}
