package ch.ranil.aoc

import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

fun <T> T.alsoPrint(): T {
    println(this)
    return this
}

fun LongRange.rangeOverlap(other: LongRange): LongRange {
    val first = max(this.first, other.first)
    val last = min(this.last, other.last)

    return if (first > last) {
        LongRange.EMPTY
    } else {
        first..last
    }
}

fun List<Int>.product(): Int {
    return this.reduce { acc, t -> acc * t }
}

fun lcm(number1: Long, number2: Long): Long {
    if (number1 == 0L || number2 == 0L) {
        return 0
    }
    val absNumber1 = abs(number1)
    val absNumber2 = abs(number2)
    val absHigherNumber = max(absNumber1, absNumber2)
    val absLowerNumber = min(absNumber1, absNumber2)
    var lcm = absHigherNumber
    while (lcm % absLowerNumber != 0L) {
        lcm += absHigherNumber
    }
    return lcm
}

class UtilsTests {
    @Test
    fun rangeOverlap() {
        assertEquals(LongRange.EMPTY, (0L..1L).rangeOverlap(2L..3L))
        assertEquals(1L..2L, (0L..2L).rangeOverlap(1L..3L))
        assertEquals(1L..3L, (0L..4L).rangeOverlap(1L..3L))
    }

    @Test
    fun product() {
        assertEquals(288, listOf(4, 8, 9).product())
    }
}
