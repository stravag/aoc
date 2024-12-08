package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day05 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(143, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(5991, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(123, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(5479, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val (rules, updates) = parse(input)

        return updates
            .filter { rules.obeyedBy(it) }
            .sumOf { it[it.size / 2] }
            .toLong()
    }

    private fun compute2(input: List<String>): Long {
        val (rules, updates) = parse(input)

        return updates
            .filterNot { rules.obeyedBy(it) }
            .map {
                it.sortedWith { i1, i2 -> if (rules.obeyedBy(listOf(i1, i2))) 1 else -1 }
            }
            .sumOf { it[it.size / 2] }
            .toLong()
    }


    private fun parse(input: List<String>): Pair<Rules, List<List<Int>>> {
        val (rawRules, rawUpdates) = input
            .partition { it.contains("|") }

        val rules = Rules.parse(rawRules)
        val updates = rawUpdates
            .filter { it.isNotEmpty() }
            .map { it.split(",").map(String::toInt) }

        return rules to updates
    }

    data class Rules(
        private val mustBeAfter: Map<Int, List<Int>>,
    ) {
        fun obeyedBy(update: List<Int>): Boolean {
            val seen = mutableSetOf<Int>()
            update.forEach { number ->
                val notOk = mustBeAfter[number].orEmpty().any { it in seen }
                if (notOk) return false
                seen.add(number)
            }
            return true
        }

        companion object {
            fun parse(input: List<String>): Rules {
                val map: Map<Int, List<Int>> = input
                    .map { it.split("|").map(String::toInt) }
                    .groupingBy { (l, _) -> l }
                    .aggregate { _, acc, (_, r), _ -> acc.orEmpty() + r }

                return Rules(map)
            }
        }
    }
}
