package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printlnColor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day23 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(7, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(1485, compute1(puzzleInput))
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

    private fun compute1(input: List<String>): Int {
        val computers = mutableMapOf<String, Computer>()
        input
            .map { it.split("-") }
            .forEach { (s1, s2) ->
                val c1 = computers.getOrDefault(s1, Computer(s1))
                val c2 = computers.getOrDefault(s2, Computer(s2))
                c1.connections.add(c2)
                c2.connections.add(c1)
                computers[s1] = c1
                computers[s2] = c2
            }

        return countNetworksOfThree(computers)
    }

    private fun countNetworksOfThree(computers: Map<String, Computer>): Int {
        val tNames = computers.keys.filter { it.startsWith("t") }
        val loops = mutableSetOf<Set<Computer>>()
        tNames
            .sorted()
            .map { computers.getValue(it) }
            .forEach { computer ->
                Debug.debug { println("Processing ${computer.name}...") }
                countNetworksOfThreeRec(computers, loops, listOf(computer))
            }

        return loops.size
    }

    private fun countNetworksOfThreeRec(
        computers: Map<String, Computer>,
        loops: MutableSet<Set<Computer>>,
        networkSoFar: List<Computer>,
    ) {
        if (networkSoFar.size == 4) {
            when {
                networkSoFar.first() == networkSoFar.last() -> {
                    Debug.debug { printlnColor("Found network with size 3: $networkSoFar", PrintColor.GREEN) }
                    loops.add(networkSoFar.toSet())
                    return
                }

                else -> {
                    Debug.debug { printlnColor("Exceeding limit, aborting: $networkSoFar", PrintColor.RED) }
                    return
                }
            }
        }
        Debug.debug { println("Network so far: $networkSoFar") }
        val nextComputers = networkSoFar.last().connections
        return nextComputers
            .filterNot {
                networkSoFar.size == 2 && it == networkSoFar.first()
            }
            .forEach { nextComputer ->
                countNetworksOfThreeRec(computers, loops, networkSoFar + nextComputer)
            }
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    data class Computer(val name: String) {
        val connections = mutableSetOf<Computer>()
    }
}
