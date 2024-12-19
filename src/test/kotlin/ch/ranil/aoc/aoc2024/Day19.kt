package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printlnColor
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

class Day19 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(6, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(206, compute1(puzzleInput))
    }

    @Test
    fun part2Dummy() {
        Debug.enable()

        val desiredDesign = "abcdabcd"
        val availableTowels = setOf("a", "b", "c", "d", "ab", "bc", "cd", "abc")
        assertEquals(36, variationsForDesign(desiredDesign, availableTowels))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(16, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(622121814629343, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val towels = input.first().split(", ").toSet()
        return input.drop(2)
            .count { desiredDesign ->
                designPossible(desiredDesign, towels)
            }
    }

    private fun compute2(input: List<String>): Long {
        val towels = input.first().split(", ").toSet()
        return input.drop(2)
            .sumOf { desiredDesign ->
                variationsForDesign(desiredDesign, towels)
            }
    }

    private fun designPossible(
        desiredDesign: String,
        towels: Set<String>,
    ): Boolean {

        val seen = mutableSetOf<String>()
        val stack: Stack<String> = Stack()
        towels
            .filter { desiredDesign.startsWith(it) }
            .forEach {
                stack.push(it)
            }
        while (!stack.empty()) {
            val currentDesign = stack.pop()
            seen.add(currentDesign)
            if (currentDesign == desiredDesign) return true

            val remainingDesign = desiredDesign.substringAfter(currentDesign)
            towels
                .filter { towel -> remainingDesign.startsWith(towel) }
                .map { towel -> "$currentDesign$towel" }
                .filter { nextDesign -> nextDesign !in seen }
                .forEach { nextDesign ->
                    stack.push(nextDesign)
                }
        }
        return false
    }

    private fun variationsForDesign(
        desiredDesign: String,
        availableTowels: Set<String>,
        cache: MutableMap<String, Long> = mutableMapOf()
    ): Long {
        Debug.debug { println("Processing solution: $desiredDesign") }
        if (desiredDesign in cache) {
            Debug.debug { printlnColor("Known solution found", PrintColor.YELLOW) }
            return cache.getValue(desiredDesign)
        }
        if (desiredDesign.isEmpty()) {
            Debug.debug { printlnColor("Found solution", PrintColor.GREEN) }
            return 1
        }
        val variations = availableTowels
            .filter { desiredDesign.startsWith(it) }
            .sumOf { towel ->
                val remainingDesign = desiredDesign.substringAfter(towel)
                variationsForDesign(
                    desiredDesign = remainingDesign,
                    availableTowels = availableTowels,
                    cache = cache
                )
            }
        cache[desiredDesign] = variations
        return variations
    }
}
