package ch.ranil.aoc.common.types

import kotlin.collections.map
import kotlin.math.abs

data class Point(val row: Int, val col: Int) : Comparable<Point> {

    override fun compareTo(other: Point): Int {
        return compareValuesBy(this, other, Point::row, Point::col)
    }

    override fun toString(): String = "($row,$col)"

    fun north(step: Int = 1) = Point(row - step, col)
    fun east(step: Int = 1) = Point(row, col + step)
    fun south(step: Int = 1) = Point(row + step, col)
    fun west(step: Int = 1) = Point(row, col - step)
    fun northWest(step: Int = 1) = Point(row - step, col - step)
    fun northEast(step: Int = 1) = Point(row - step, col + step)
    fun southWest(step: Int = 1) = Point(row + step, col - step)
    fun southEast(step: Int = 1) = Point(row + step, col + step)

    fun edges(): List<Point> {
        return listOf(
            // Above
            Point(row - 1, col - 1),
            Point(row - 1, col),
            Point(row - 1, col + 1),
            // Side
            Point(row, col - 1),
            Point(row, col + 1),
            // Below
            Point(row + 1, col - 1),
            Point(row + 1, col),
            Point(row + 1, col + 1),
        )
    }

    fun directEdges(): List<Point> {
        return listOf(
            north(),
            east(),
            south(),
            west(),
        )
    }

    fun move(direction: Direction, steps: Int = 1): Point {
        return when (direction) {
            Direction.N -> copy(row = row - steps)
            Direction.E -> copy(col = col + steps)
            Direction.S -> copy(row = row + steps)
            Direction.W -> copy(col = col - steps)
        }
    }

    fun diffTo(other: Point): Pair<Int, Int> {
        return (other.row - row) to (other.col - col)
    }

    fun distanceTo(other: Point): Int {
        return abs(other.col - col) + abs(other.row - row)
    }

    fun isAdjacentTo(other: Point): Boolean {
        return (abs(other.col - col) <= 1) and (abs(other.row - row) <= 1)
    }

    fun allPointsTo(other: Point): List<Point> {
        val minCol = minOf(this.col, other.col)
        val maxCol = maxOf(this.col, other.col)
        val minRow = minOf(this.row, other.row)
        val maxRow = maxOf(this.row, other.row)
        return when {
            row == other.row -> (minCol..maxCol).map { Point(row, it) }
            col == other.col -> (minRow..maxRow).map { Point(it, col) }
            else -> error("Points $this, $other not vertical or horizontal")
        }
    }

    companion object {
        val directions: List<MovePointBySteps> = listOf(
            Point::north,
            Point::east,
            Point::south,
            Point::west,
            Point::northWest,
            Point::northEast,
            Point::southWest,
            Point::southEast,
        )
    }
}
