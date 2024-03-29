package ch.ranil.aoc

import ch.ranil.aoc.aoc2023.Point
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

enum class PrintColor(val code: String) {
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
}

fun <T> printColor(color: PrintColor, v: T) {
    val reset = "\u001b[0m"
    print(color.code + v + reset)
}

fun <T> printlnColor(color: PrintColor, v: T) {
    printColor(color, v)
    println()
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

fun List<String>.print(printChar: (Point, Char) -> Unit = { _, c -> print(c) }) {
    forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            printChar(Point(x, y), c)
        }
        println()
    }
}

fun Int.isEven(): Boolean = this % 2 == 0

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

fun <T> bfs(
    start: T,
    stopCriteria: (T) -> Boolean = { false },
    skipCriteria: (T) -> Boolean = { false },
    next: (T) -> Collection<T>,
): Set<T> {
    val seen = mutableSetOf<T>()
    val queue = mutableListOf(start)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        if (stopCriteria(current)) return seen
        if (skipCriteria(current)) continue
        next(current)
    }
    return seen
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
