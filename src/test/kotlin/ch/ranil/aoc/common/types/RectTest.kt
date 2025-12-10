package ch.ranil.aoc.common.types

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class RectTest {
    @Test
    fun corners() {
        val rect = Rect(Point(2, 2), Point(0, 0))
        assertEquals(Point(0, 0), rect.topLeft)
        assertEquals(Point(0, 2), rect.topRight)
        assertEquals(Point(2, 2), rect.bottomRight)
        assertEquals(Point(2, 0), rect.bottomLeft)
        assertEquals(
            setOf(
                Point(0, 0),
                Point(0, 2),
                Point(2, 2),
                Point(2, 0),
            ), rect.corners
        )
    }

    @Test
    fun cornersOfMinimalRect() {
        val rect = Rect(Point(0, 0), Point(0, 0))
        assertEquals(Point(0, 0), rect.topLeft)
        assertEquals(Point(0, 0), rect.topRight)
        assertEquals(Point(0, 0), rect.bottomRight)
        assertEquals(Point(0, 0), rect.bottomLeft)
        assertEquals(setOf(Point(0, 0)), rect.corners)
    }

    @Test
    fun shrink() {
        val rect = Rect(Point(0, 0), Point(2, 2))
        assertTrue(rect.canShrink())
        assertEquals(Rect(Point(1, 1), Point(1, 1)), rect.shrink())
    }

    @Test
    fun cannotShrinkSmallRects() {
        fun test(rect: Rect) {
            assertThrows<IllegalStateException> { rect.shrink() }
            assertFalse(rect.canShrink())
        }

        val rect1x1 = Rect(Point(0, 0), Point(0, 0))
        val rect2x2 = Rect(Point(0, 0), Point(1, 1))
        val rect2x1 = Rect(Point(0, 0), Point(0, 1))
        val rect1x2 = Rect(Point(0, 0), Point(1, 0))
        test(rect1x1)
        test(rect2x2)
        test(rect2x1)
        test(rect1x2)
    }

    @Test
    fun contains() {
        val rect = Rect(Point(0, 0), Point(2, 2))
        val center = Point(1, 1)
        assertFalse(Point(3, 3) in rect)
        assertTrue(center in rect)
        center.edges().forEach { assertTrue(it in rect) }
    }

    @Test
    fun area() {
        assertEquals(9, Rect(Point(0, 0), Point(2, 2)).area)
        assertEquals(1, Rect(Point(0, 0), Point(0, 0)).area)
    }

    @Test
    fun equality() {
        val tlbr = Rect(Point(0, 0), Point(2, 2))
        val brtl = Rect(Point(2, 2), Point(0, 0))
        val bltr = Rect(Point(2, 0), Point(0, 2))
        val trbl = Rect(Point(0, 2), Point(2, 0))
        assertEquals(1, setOf(tlbr, brtl, bltr, trbl).size)
    }

    @Test
    fun edgePoints() {
        val rect1x1 = Rect(Point(0, 0), Point(0, 0))
        val rect2x2 = Rect(Point(0, 0), Point(1, 1))
        assertEquals(setOf(Point(0, 0)), rect1x1.edgePoints)
        assertEquals(rect2x2.corners, rect2x2.edgePoints)
    }
}
