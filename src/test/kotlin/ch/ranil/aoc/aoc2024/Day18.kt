package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class Day18 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()

        assertEquals(22, compute1(size = 7, take = 12, testInput))
    }

    @Test
    fun part1Puzzle() {
        Debug.enable()
        assertEquals(0, compute1(size = 71, take = 1024, puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(size: Int, take: Int, input: List<String>): Int {
        val memory = Memory(size)
        memory.addCorruptions(take, input)
        memory.printMemory()
        val distances = memory.bfs()
        return distances[memory.end] ?: error("no path found")
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private class Memory(val size: Int) {
        val start = Point(0, 0)
        val end = Point(size - 1, size - 1)

        private val corrupted: MutableSet<Point> = mutableSetOf()

        fun addCorruptions(take: Int, input: List<String>) {
            input
                .take(take)
                .map { line -> "[0-9]+".toRegex().findAll(line).map { it.value.toInt() }.toList() }
                .forEach { (x, y) ->
                    corrupted.add(Point(x, y))
                }
        }

        fun Point.isInMemory(): Boolean {
            return x in 0..<size && y in 0..<size
        }

        fun bfs(): Map<Point, Int> {
            val seen = mutableMapOf<Point, Int>()
            val queue = LinkedList<Point>()
            seen[start] = 0
            queue.add(start)
            while (queue.isNotEmpty()) {
                val current = queue.poll()
                val currentDistance = seen.getValue(current)
                val neighbors = current.directEdges()
                    .filter { it.isInMemory() }
                    .filter { it !in corrupted }
                    .filter { (seen[it] ?: Int.MAX_VALUE) > currentDistance + 1 }

                neighbors.forEach {
                    seen[it] = currentDistance + 1
                    queue.add(it)
                }
            }
            return seen
        }

        fun navigate(): Int {
            var minSteps = Int.MAX_VALUE
            val seen = mutableMapOf<Point, Int>()
            val queue = PriorityQueue(compareBy(Path::steps))
            queue.add(Path(Point(0, 0), 0))
            while (queue.isNotEmpty()) {
                val current = queue.poll()

                val bestKnownSteps = seen.getOrDefault(current.point, Int.MAX_VALUE)
                if (current.steps <= bestKnownSteps) {
                    seen[current.point] = current.steps
                } else {
                    continue
                }

                if (current.point == Point(size - 1, size - 1)) {
                    Debug.debug {
                        println("Found a path in ${current.steps} steps")
                        printMemory(current)
                    }
                    if (current.steps > minSteps) {
                        break
                    }
                    minSteps = current.steps
                }

                if (current.steps > bestKnownSteps) continue // don't pursue suboptimal paths

                val nextPathPoints = current.point
                    .directEdges()
                    .filter { it.isInMemory() } // stay in memory
                    .filter { it !in seen }
                    .filterNot { it in corrupted } // avoid corrupted
                    .map { Path(it, current.steps + 1) }

                queue.addAll(nextPathPoints)
                Debug.debug {
                    if (queue.size > 70_000) {
                        println("Queue: $queue")
                        println("Queue size: ${queue.size}")
                        error("something is off")
                    }
                }
            }

            return minSteps
        }

        fun printMemory() = printMemory(Path(Point(-1, -1), 0))
        fun printMemory(current: Path) {
            for (y in 0..<size) {
                for (x in 0..<size) {
                    val point = Point(x, y)
                    when (point) {
                        current.point -> printColor('O', PrintColor.GREEN)
                        in corrupted -> printColor('#', PrintColor.RED)
                        else -> print('.')
                    }

                }
                println()
            }
        }

        private data class Path(
            val point: Point,
            val steps: Int,
        )
    }
}
