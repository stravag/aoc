package ch.ranil.aoc.common.types

import ch.ranil.aoc.common.*

abstract class AbstractMap(
    private val input: List<String>,
) {
    fun allPoints(): List<Point> {
        return input.flatMapIndexed { y, s ->
            s.mapIndexed { x, _ ->
                Point(y, x)
            }
        }
    }

    fun forEach(action: (Point, Char) -> Unit) {
        return input.forEachPointWithChar(action)
    }

    fun printMap(border: Boolean = true, printChar: (Point, Char) -> Unit) {
        input.print(border, printChar)
    }

    fun charFor(point: Point): Char {
        return input.charForOrNull(point) ?: error("ch.ranil.aoc.aoc2024.Point not in map!")
    }

    fun charForOrNull(point: Point): Char? {
        return input.charForOrNull(point)
    }

    fun isPointInMap(point: Point): Boolean {
        return input.containsPoint(point)
    }
}