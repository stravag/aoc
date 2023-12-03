package ch.ranil.aoc.aoc2023

import kotlin.math.abs

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
