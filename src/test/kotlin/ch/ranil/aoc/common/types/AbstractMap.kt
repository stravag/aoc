package ch.ranil.aoc.common.types

import ch.ranil.aoc.common.*

abstract class AbstractMap(
    private val input: List<String>,
) {
    fun allPoints(): List<Point> {
        return input.flatMapIndexed { y, s ->
            s.mapIndexed { x, c ->
                Point(x, y)
            }
        }
    }

    fun forEach(action: (Point, Char) -> Unit) {
        return input.forEachPointWithChar(action)
    }

    fun printMap(printChar: (Point, Char) -> Unit) {
        input.print(printChar)
    }

    fun charFor(point: Point): Char {
        return input.charForPoint(point) ?: error("Point not in map!")
    }

    fun charForOrNull(point: Point): Char? {
        return input.charForPoint(point)
    }

    fun isPointInMap(point: Point): Boolean {
        return input.containsPoint(point)
    }
}