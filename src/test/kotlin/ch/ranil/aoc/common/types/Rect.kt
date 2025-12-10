package ch.ranil.aoc.common.types

import kotlin.math.absoluteValue
import kotlin.ranges.rangeTo

data class Rect(
    val p1: Point,
    val p2: Point
) {
    constructor(pointPair: Pair<Point, Point>) : this(pointPair.first, pointPair.second)

    val topLeft: Point get() = Point(row = minOf(p1.row, p2.row), col = minOf(p1.col, p2.col))
    val topRight: Point get() = Point(row = minOf(p1.row, p2.row), col = maxOf(p1.col, p2.col))
    val bottomLeft: Point get() = Point(row = maxOf(p1.row, p2.row), col = minOf(p1.col, p2.col))
    val bottomRight: Point get() = Point(row = maxOf(p1.row, p2.row), col = maxOf(p1.col, p2.col))

    val width: Long get() = 1L + (topRight.col - topLeft.col).absoluteValue
    val height: Long get() = 1L + (bottomLeft.row - topLeft.row).absoluteValue

    val corners: Set<Point> get() = setOf(topLeft, topRight, bottomRight, bottomLeft)

    val area: Long get() = width * height

    override fun toString(): String = "($p1,$p2)"

    override fun equals(other: Any?): Boolean {
        if (other !is Rect) return false
        return corners.containsAll(other.corners)
    }

    override fun hashCode(): Int {
        return corners.hashCode()
    }

    operator fun contains(point: Point): Boolean {
        return point.row in topLeft.row..bottomRight.row && point.col in topLeft.col..bottomRight.col
    }

    fun shrink(): Rect {
        check(width > 2 && height > 2) { "Cannot shrink rect with width or height <= 2" }
        return Rect(topLeft.southEast(), bottomRight.northWest())
    }
}
