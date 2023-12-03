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

class PointTest {
    @Test
    fun testEdgesAndAdjacent() {
        val center = Point(0, 0)
        assertTrue(center.edges().all { edge -> edge.isAdjacentTo(center) })
    }

    @Test
    fun testEdgesUnique() {
        assertEquals(8, Point(0, 0).edges().distinct().size)
    }
}
