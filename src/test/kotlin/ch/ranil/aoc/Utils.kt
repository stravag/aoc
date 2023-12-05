package ch.ranil.aoc

import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

fun LongRange.overlap(other: LongRange): LongRange {
    val first = max(this.first, other.first)
    val last = min(this.last, other.last)

    return if (first > last) {
        LongRange.EMPTY
    } else {
        first..last
    }
}

class RangeTests {
    @Test
    fun overlap() {
        assertEquals(LongRange.EMPTY, (0L..1L).overlap(2L..3L))
        assertEquals(1L..2L, (0L..2L).overlap(1L..3L))
        assertEquals(1L..3L, (0L..4L).overlap(1L..3L))
    }
}