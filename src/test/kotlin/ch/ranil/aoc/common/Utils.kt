package ch.ranil.aoc.common

import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.test.assertEquals

enum class PrintColor(val code: String) {
    RED("\u001b[31m"),
    GREEN("\u001b[32m"),
    YELLOW("\u001b[33m"),
    BLUE("\u001b[34m"),
}

fun <T> printColor(v: T, color: PrintColor) {
    val reset = "\u001b[0m"
    print(color.code + v + reset)
}

fun <T> printlnColor(v: T, color: PrintColor) {
    printColor(v, color)
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

fun List<String>.forEachPointWithChar(action: (Point, Char) -> Unit) {
    return this.forEachIndexed { row, s ->
        s.forEachIndexed { col, c ->
            action(Point(row, col), c)
        }
    }
}

fun List<String>.allPoints(): List<Point> {
    return flatMapIndexed { y, s ->
        s.mapIndexed { x, _ ->
            Point(y, x)
        }
    }
}

fun List<String>.print(border: Boolean = true, printChar: (Point, Char) -> Unit = { _, c -> print(c) }) {
    if (border) println("+" + "-".repeat(first().length) + "+")
    forEachIndexed { row, s ->
        s.forEachIndexed { col, c ->
            if (border) if (col == 0) print('|')
            printChar(Point(row, col), c)
            if (border) if (col == s.length - 1) print('|')
        }
        println()
    }
    if (border) println("+" + "-".repeat(first().length) + "+")
}

fun List<String>.charFor(point: Point): Char {
    return this.getOrNull(point.row)?.getOrNull(point.col) ?: error("No char for point $point")
}

fun List<String>.charForOrNull(point: Point): Char? {
    return this.getOrNull(point.row)?.getOrNull(point.col)
}

fun List<String>.containsPoint(point: Point): Boolean {
    return point.row in indices && point.col in first().indices
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
