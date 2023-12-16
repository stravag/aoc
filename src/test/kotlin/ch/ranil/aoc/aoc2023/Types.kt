package ch.ranil.aoc.aoc2023

import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface Coordinate {
    val x: Int
    val y: Int
}

data class Point(override val x: Int, override val y: Int) : Coordinate {
    override fun toString(): String = "($x,$y)"

    fun north() = Point(x, y - 1)
    fun east() = Point(x + 1, y)
    fun south() = Point(x, y + 1)
    fun west() = Point(x - 1, y)
}

fun Coordinate.isAdjacentTo(other: Point): Boolean {
    return (abs(other.x - this.x) <= 1) and (abs(other.y - this.y) <= 1)
}

fun <T> Coordinate.edges(coordinateConstructor: (Int, Int) -> T): List<T> {
    return listOf(
        // Above
        coordinateConstructor(x - 1, y - 1),
        coordinateConstructor(x, y - 1),
        coordinateConstructor(x + 1, y - 1),
        // Side
        coordinateConstructor(x - 1, y),
        coordinateConstructor(x + 1, y),
        // Below
        coordinateConstructor(x - 1, y + 1),
        coordinateConstructor(x, y + 1),
        coordinateConstructor(x + 1, y + 1),
    )
}

fun Point.distanceTo(other: Point): Int {
    return abs(other.x - x) + abs(other.y - y)
}

class PointTest {
    @Test
    fun testEdgesAndAdjacent() {
        val center = Point(0, 0)
        assertTrue(center.edges(::Point).all { edge -> edge.isAdjacentTo(center) })
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
        assertEquals(8, Point(0, 0).edges(::Point).distinct().size)
    }
}
