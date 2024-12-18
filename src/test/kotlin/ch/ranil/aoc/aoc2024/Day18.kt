package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.math.floor
import kotlin.test.assertEquals

class Day18 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(22, compute1(size = 7, corruptionCount = 12, testInput))
    }

    @Test
    fun part1Puzzle() {
        Debug.enable()
        assertEquals(284, compute1(size = 71, corruptionCount = 1024, puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals("6,1", compute2(size = 7, corruptionCount = 12, testInput))
    }

    @Test
    fun part2Puzzle() {
        Debug.enable()
        assertEquals("51,50", compute2(size = 71, corruptionCount = 1024, puzzleInput))
    }

    private fun compute1(size: Int, corruptionCount: Int, input: List<String>): Int {
        val memory = Memory(size, input)
        val path = memory.shortestPathToEnd(corruptionCount)
        memory.printMemory(path)
        return path.size - 1
    }

    private fun compute2(size: Int, corruptionCount: Int, input: List<String>): String {
        val memory = Memory(size, input)

        // binary search
        var l = corruptionCount
        var r = input.size - 1
        while (l <= r) {
            val m = floor((l + r) / 2.0).toInt()
            val mPath = memory.shortestPathToEnd(m)
            val nPath = memory.shortestPathToEnd(m + 1)
            if (mPath.isNotEmpty() && nPath.isNotEmpty()) {
                l = m + 1
            } else if (mPath.isEmpty() && nPath.isEmpty()) {
                r = m - 1
            } else {
                val i = listOf(mPath to m, nPath to m + 1)
                    .single { it.first.isNotEmpty() }.second
                return input[i]
            }
        }
        error("nothing found")
    }

    private class Memory(val size: Int, private val input: List<String>) {
        val start = Point(0, 0)
        val end = Point(size - 1, size - 1)

        private val corrupted: MutableSet<Point> = mutableSetOf()

        fun shortestPathToEnd(corruptionCount: Int): Set<Point> {
            corrupted.clear()
            addCorruptions(corruptionCount)
            return shortestPathToEnd()
        }

        private fun shortestPathToEnd(): Set<Point> {
            val seen = mutableMapOf(start to setOf(start))
            val queue = LinkedList<Point>()
            queue.add(start)
            while (queue.isNotEmpty()) {
                val current = queue.poll()
                val neighbors = current.directEdges()
                    .filter { it in this }
                    .filter { it !in corrupted }
                    .filter { it !in seen }

                neighbors.forEach { next ->
                    queue.add(next)
                    seen[next] = seen.getValue(current) + next
                }
            }

            return seen[end].orEmpty()
        }

        private fun addCorruptions(corruptionCount: Int) {
            input
                .take(corruptionCount)
                .map { line -> "[0-9]+".toRegex().findAll(line).map { it.value.toInt() }.toList() }
                .forEach { (x, y) ->
                    corrupted.add(Point(x, y))
                }
        }

        private operator fun contains(point: Point): Boolean {
            return point.x in 0..<size && point.y in 0..<size
        }

        fun printMemory(path: Collection<Point>) {
            for (y in 0..<size) {
                for (x in 0..<size) {
                    val point = Point(x, y)
                    when (point) {
                        in path -> printColor('O', PrintColor.GREEN)
                        in corrupted -> printColor('#', PrintColor.RED)
                        else -> print('.')
                    }
                }
                println()
            }
            println()
        }
    }
}
