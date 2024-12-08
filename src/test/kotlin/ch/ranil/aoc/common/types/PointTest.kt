package ch.ranil.aoc.common.types

import ch.ranil.aoc.common.containsPoint
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

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
    fun testDiffTo() {
        val zero = Point(0, 0)
        assertEquals(0 to 0 , zero.diffTo(zero))
        assertEquals(0 to 1, zero.diffTo(Point(0, 1)))
        assertEquals(-1 to 0, zero.diffTo(Point(-1, 0)))
        assertEquals(0 to -1, zero.diffTo(Point(0, -1)))
        assertEquals(1 to 1, zero.diffTo(Point(1, 1)))
    }

    @Test
    fun testDistance() {
        val zero = Point(0, 0)
        assertEquals(0 , zero.distanceTo(zero))
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
        assertTrue(board.containsPoint(Point(1, 1)))

        assertFalse(board.containsPoint(Point(-1, 0)))
        assertFalse(board.containsPoint(Point(0, -1)))
        assertFalse(board.containsPoint(Point(2, 0)))
        assertFalse(board.containsPoint(Point(0, 2)))
    }
}