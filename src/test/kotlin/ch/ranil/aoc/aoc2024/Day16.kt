package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.AbstractMap
import ch.ranil.aoc.common.types.Direction
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class Day16 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(7036, compute1(testInput))
        assertEquals(11048, compute1(test2Input))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(115500, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(45, compute2(testInput))
        assertEquals(64, compute2(test2Input))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(679, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val maze = Maze(input)
        return maze.navigate().first
    }

    private fun compute2(input: List<String>): Int {
        val maze = Maze(input)
        return maze.navigate().second

    }

    private class Maze(input: List<String>) : AbstractMap(input) {
        val start: Point = allPoints().single { charFor(it) == 'S' }
        val direction: Direction = Direction.E

        fun navigate(): Pair<Int, Int> {
            var minCost = Int.MAX_VALUE
            val seen = mutableMapOf<Pair<Point, Direction>, Int>()
            val bestSeats = mutableSetOf<Point>()
            val queue = PriorityQueue(compareBy(Path::cost))
            queue.add(Path(listOf(start), direction, 0))
            while (queue.isNotEmpty()) {
                val pathPoint = queue.poll()

                val node = pathPoint.path.last() to pathPoint.direction
                val bestKnownCost = seen.getOrDefault(node, Int.MAX_VALUE)
                if (pathPoint.cost < bestKnownCost) {
                    seen[node] = pathPoint.cost
                }

                if (charFor(pathPoint.path.last()) == 'E') {
                    if (pathPoint.cost > minCost) {
                        break
                    }
                    minCost = pathPoint.cost
                    bestSeats.addAll(pathPoint.path)
                }

                if (pathPoint.cost > bestKnownCost) continue // don't pursue suboptimal paths

                val nextPathPoints = listOf(
                    pathPoint.moveForward(),
                    pathPoint.moveRight(),
                    pathPoint.moveLeft(),
                ).filter { charFor(it.path.last()) != '#' }

                queue.addAll(nextPathPoints)
            }

            printMaze(bestSeats)
            return minCost to bestSeats.size
        }

        private fun printMaze(path: Set<Point>) {
            this.printMap { point, c ->
                when {
                    c == 'E' -> printColor(c, PrintColor.GREEN)
                    c == 'S' -> printColor(c, PrintColor.GREEN)
                    c == '#' -> printColor(c, PrintColor.RED)
                    point in path -> printColor('O', PrintColor.YELLOW)
                    else -> print(c)
                }
            }
        }
    }

    private data class Path(
        val path: List<Point>,
        val direction: Direction,
        val cost: Int,
    ) {
        fun moveForward(): Path {
            return copy(
                path = path + path.last().move(direction),
                cost = cost + 1
            )
        }

        fun moveRight(): Path {
            return copy(
                path = path + path.last().move(direction.turnRight()),
                direction = direction.turnRight(),
                cost = cost + 1001
            )
        }

        fun moveLeft(): Path {
            return copy(
                path = path + path.last().move(direction.turnLeft()),
                direction = direction.turnLeft(),
                cost = cost + 1001
            )
        }
    }
}
