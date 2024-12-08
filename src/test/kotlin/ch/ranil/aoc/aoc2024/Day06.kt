package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.*
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Direction.*
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day06 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(41, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(4663, compute1(puzzleInput))
    }

    @Test
    fun part2TestDummy() {
        val input = """
            .#.
            ..#
            .^.
            .#.
        """.trimIndent().lines()
        assertEquals(1, compute2(input, debug = true))
    }

    @Test
    fun part2Test() {
        assertEquals(6, compute2(testInput, debug = true))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1530, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val path = Map(input).walk()
        return path.count()
    }


    private fun compute2(input: List<String>, debug: Boolean = false): Int {
        val path = Map(input).walk()
        return path
            .drop(1) // no obstruction on starting position
            .sumOf { pointOnPath ->
                Map(input, addedObstacle = pointOnPath).isLoop(debug)
            }
    }

    class Map(
        private val board: List<String>,
        private val addedObstacle: Point? = null,
    ) {
        private val startingPoint: Point = board
            .allPointsWithChar()
            .first { (_, c) -> c == '^' }
            .first

        private fun isObstacle(point: Point) = board.charForPoint(point) == '#' || addedObstacle == point
        private fun contains(point: Point): Boolean = board.containsPoint(point)

        fun walk(): Set<Point> {
            var point = startingPoint
            var direction = N
            val path = mutableSetOf<Point>()

            while (contains(point)) {
                path.add(point)
                val p = point.move(direction)
                when {
                    isObstacle(p) -> direction = direction.turn90()
                    else -> point = p
                }
            }

            println("Walked Path")
            this.print(path)

            return path
        }

        fun isLoop(debug: Boolean): Int {
            var point = startingPoint
            var direction = N
            val path = mutableSetOf<Pair<Point, Direction>>()
            while (path.add(point to direction)) {
                val p = point.move(direction)
                when {
                    isObstacle(p) -> direction = direction.turn90()
                    !contains(p) -> return 0
                    else -> point = p
                }
            }

            if (debug) {
                println("Found Loop")
                this.print(path)
            }

            return 1
        }

        @JvmName("print2")
        private fun print(seen: Set<Pair<Point, Direction>>) = this.print(seen.map { it.first }.toSet())
        private fun print(seen: Set<Point>) {
            val firstPoint = seen.firstOrNull() ?: Point(1, -1)
            val lastPoint = seen.lastOrNull() ?: Point(1, -1)
            board.print { p, c ->
                when {
                    p == firstPoint -> printColor(PrintColor.GREEN, 'X')
                    p == lastPoint -> printColor(PrintColor.RED, 'X')
                    seen.contains(p) -> printColor(PrintColor.YELLOW, 'X')
                    p == addedObstacle -> print('O')
                    else -> print(c)
                }
            }
        }
    }
}
