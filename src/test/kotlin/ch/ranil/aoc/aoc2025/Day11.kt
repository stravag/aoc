package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printlnColor
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.util.LinkedList
import java.util.Queue
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
    @Disabled
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val connections = input.associate { row ->
            val elements = row.split(" ")
            val fromNode = elements[0].dropLast(1)
            val toNodes = elements.drop(1).toSet()
            fromNode to toNodes
        }
        return findPaths(connections)
    }

    private fun compute2(input: List<String>): Long {
        val connections = input.associate { row ->
            val elements = row.split(" ")
            val fromNode = elements[0].dropLast(1)
            val toNodes = elements.drop(1).toSet()
            fromNode to toNodes
        }
        return findPathsViaFftDac(connections)
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
        val start = "svr"
        val end = "out"
        val startKey = Key(start, passedFft = false, passedDac = false)
        val queue: Queue<Key> = LinkedList<Key>().apply { offer(startKey) }
        var paths = 0L
        while (queue.isNotEmpty()) {
            val (currentNode, passedFft, passedDac) = queue.poll()
            Debug.debug {
                println("Currently at: $currentNode")
            }
            if (currentNode == end) {
                if (passedFft && passedDac) {
                    Debug.debug {
                        printlnColor("Found a Path with fft & dac", PrintColor.GREEN)
                    }
                    paths++
                } else {
                    Debug.debug {
                        printlnColor("Found a Path", PrintColor.RED)
                    }
                }
            } else {
                for (nextNode in connections.getValue(currentNode)) {
                    val nextKey = Key(nextNode, passedFft || nextNode == "fft", passedDac || nextNode == "dac")
                    queue.offer(nextKey)
                }
            }
        }

        return paths
    }

    data class Key(
        val node: String,
        val passedFft: Boolean,
        val passedDac: Boolean
    )
}
