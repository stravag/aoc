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
    @Disabled
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
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
                for (nextNode in connections[currentNode].orEmpty()) {
                    queue.offer(nextNode)
                }
            }
        }

        return paths
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private data class Button(
        val name: String,
        val togglesLights: Set<Int>,
    ) {
        fun press(lights: Lights): Lights {
            val newLights = lights
                .lights
                .mapIndexed { i, l ->
                    when {
                        i in togglesLights -> !l
                        else -> l
                    }
                }
            return Lights(newLights)
        }


        companion object {
            fun parse(input: String): Button {
                val togglesLights = input
                    .drop(1).dropLast(1)
                    .split(",")
                    .map { it.toInt() }
                    .toSet()
                return Button(input, togglesLights)
            }
        }
    }

    private data class Lights(
        val lights: List<Boolean>,
    ) {
        override fun toString(): String =
            lights
                .joinToString("") { if (it) "#" else "." }
                .let { return "[$it]" }

        companion object {
            fun parse(input: String): Lights {
                val lights = input
                    .drop(1).dropLast(1)
                    .map { c ->
                        when (c) {
                            '.' -> false
                            '#' -> true
                            else -> error("unexpected char $c")
                        }
                    }
                return Lights(lights)
            }
        }
    }
}
