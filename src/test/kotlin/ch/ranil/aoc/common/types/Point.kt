package ch.ranil.aoc.common.types

import kotlin.math.abs

data class Point(val x: Int, val y: Int) {
    override fun toString(): String = "($x,$y)"

    fun north(step: Int = 1) = Point(x, y - step)
    fun east(step: Int = 1) = Point(x + step, y)
    fun south(step: Int = 1) = Point(x, y + step)
    fun west(step: Int = 1) = Point(x - step, y)
    fun northWest(step: Int = 1) = Point(x - step, y - step)
    fun northEast(step: Int = 1) = Point(x + step, y - step)
    fun southWest(step: Int = 1) = Point(x - step, y + step)
    fun southEast(step: Int = 1) = Point(x + step, y + step)

    fun edges(): List<Point> {
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
            Direction.N -> copy(y = y - steps)
            Direction.E -> copy(x = x + steps)
            Direction.S -> copy(y = y + steps)
            Direction.W -> copy(x = x - steps)
        }
    }

    fun diffTo(other: Point): Pair<Int, Int> {
        return (other.x - x) to (other.y - y)
    }

    fun distanceTo(other: Point): Int {
        return abs(other.x - x) + abs(other.y - y)
    }

    fun isAdjacentTo(other: Point): Boolean {
        return (abs(other.x - this.x) <= 1) and (abs(other.y - this.y) <= 1)
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
