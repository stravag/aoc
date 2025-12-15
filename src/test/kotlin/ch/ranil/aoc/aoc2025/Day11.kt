package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printlnColor
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class Day11 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(5, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(466, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(2, compute2(test2Input))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(549705036748518, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val connections = getConnections(input)
        return findPaths(connections)
    }

    private fun compute2(input: List<String>): Long {
        val connections = getConnections(input)
        return findPathsViaFftDac(connections)
    }

    private fun getConnections(input: List<String>): Map<String, Set<String>> =
        input.associate { row ->
            val elements = row.split(" ")
            val fromNode = elements[0].dropLast(1)
            val toNodes = elements.drop(1).toSet()
            fromNode to toNodes
        }

    private fun findPaths(connections: Map<String, Set<String>>): Long {
        val start = "you"
        val end = "out"
        val queue: Queue<String> = LinkedList()
        queue.offer(start)
        var paths = 0L
        while (queue.isNotEmpty()) {
            val currentNode = queue.poll()
            Debug.debug {
                println("Currently at: $currentNode")
            }
            if (currentNode == end) {
                Debug.debug {
                    printlnColor("Found a Path", PrintColor.GREEN)
                }
                paths++
            } else {
                for (nextNode in connections.getValue(currentNode)) {
                    queue.offer(nextNode)
                }
            }
        }

        return paths
    }

    private fun findPathsViaFftDac(connections: Map<String, Set<String>>): Long {
        val cache = mutableMapOf<Key, Long>()
        fun dfs(key: Key): Long {
            if (key.node == "out") {
                return if (key.passedFft && key.passedDac) 1 else 0
            }
            return connections
                .getValue(key.node)
                .sumOf { nextNode ->
                    val nextKey = key.next(nextNode)
                    cache.getOrPut(nextKey) { dfs(nextKey) }
                }
        }

        val startKey = Key("svr", passedFft = false, passedDac = false)
        return dfs(startKey)
    }

    data class Key(
        val node: String,
        val passedFft: Boolean,
        val passedDac: Boolean
    ) {
        fun next(nextNode: String): Key =
            Key(nextNode, passedFft || nextNode == "fft", passedDac || nextNode == "dac")
    }
}
