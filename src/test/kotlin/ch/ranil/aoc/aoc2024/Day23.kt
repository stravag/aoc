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
        assertEquals("co,de,ka,ta", compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals("cc,dz,ea,hj,if,it,kf,qo,sk,ug,ut,uv,wh", compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val computers = buildNetworkGraph(input)
        val loops = mutableSetOf<Set<Computer>>()
        computers
            .filter { it.name.startsWith("t") }
            .forEach { computer ->
                Debug.debug { println("Processing ${computer.name}...") }
                countNetworksOfThreeRec(loops, listOf(computer))
            }

        return loops.size
    }

    private fun countNetworksOfThreeRec(
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
        return networkSoFar
            .last()
            .getConnections()
            .filterNot { networkSoFar.size == 2 && it == networkSoFar.first() } // don't move backwards
            .forEach { nextComputer ->
                countNetworksOfThreeRec(loops, networkSoFar + nextComputer)
            }
    }

    private fun compute2(input: List<String>): String {
        val computers = buildNetworkGraph(input)
        val cliques = mutableListOf<Set<Computer>>()
        findCliquesRec(
            candidates = computers.toMutableSet(),
            result = cliques
        )

        val largestClique = cliques.maxBy { it.size }
        println("Largest clique: $largestClique")
        return largestClique.sortedBy { it.name }.joinToString(",") { it.name }
    }

    /**
     * Bron Kerbosch Algorithm to find maximum cliques
     */
    private fun findCliquesRec(
        currentClique: MutableSet<Computer> = mutableSetOf(),
        candidates: MutableSet<Computer>,
        seen: MutableSet<Computer> = mutableSetOf(),
        result: MutableList<Set<Computer>>,
    ) {
        if (candidates.isEmpty() && seen.isEmpty()) {
            result.add(currentClique.toSet())
            return
        }

        val candidatesIterator = candidates.iterator()
        candidatesIterator.forEach { candidate ->
            currentClique.add(candidate)
            val neighbors = candidate.getConnections()
            findCliquesRec(
                currentClique = currentClique,
                candidates = candidates.intersect(neighbors).toMutableSet(),
                seen = seen.intersect(neighbors).toMutableSet(),
                result = result
            )
            currentClique.remove(candidate)
            candidatesIterator.remove()
            seen.add(candidate)
        }
    }

    private fun buildNetworkGraph(input: List<String>): Collection<Computer> {
        val computers = mutableMapOf<String, Computer>()
        input
            .map { it.split("-") }
            .forEach { (s1, s2) ->
                val c1 = computers.getOrDefault(s1, Computer(s1))
                val c2 = computers.getOrDefault(s2, Computer(s2))
                c1.add(c2)
                c2.add(c1)
                computers[s1] = c1
                computers[s2] = c2
            }
        return computers.values
    }

    data class Computer(val name: String) {
        private val connections = mutableSetOf<Computer>()

        fun getConnections(): Set<Computer> = connections

        fun add(connection: Computer) {
            connections.add(connection)
        }
    }
}
