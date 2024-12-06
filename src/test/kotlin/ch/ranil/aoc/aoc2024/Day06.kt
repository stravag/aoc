package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.*
import ch.ranil.aoc.Direction.*
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
        assertEquals(1, compute2(input))
    }

    @Test
    fun part2Test() {
        assertEquals(6, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(1530, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val path = Map(input).walk()
        return path.count()
    }


    private fun compute2(input: List<String>): Int {
        val path = Map(input).walk()
        return path
            .drop(1) // no obstruction on starting position
            .sumOf { pointOnPath ->
                Map(input, addedObstacle = pointOnPath).isLoop()
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

            print(path)

            return path
        }

        fun isLoop(): Int {
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

            return 1
        }

        fun print(seen: Set<Point>) {
            println()
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
