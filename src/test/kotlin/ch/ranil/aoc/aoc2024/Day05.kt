package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.AbstractDay
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
        val (rules, numbers) = parse(input)

        return numbers
            .filter { orderedCorrectly(it, rules) }
            .sumOf { it[it.size / 2] }
            .toLong()
    }

    private fun compute2(input: List<String>): Long {
        val (rules, numbers) = parse(input)

        return numbers
            .filterNot { orderedCorrectly(it, rules) }
            .map {
                it.sortedWith { i1, i2 -> if (orderedCorrectly(listOf(i1, i2), rules)) 1 else -1 }
            }
            .sumOf { it[it.size / 2] }
            .toLong()
    }


    private fun orderedCorrectly(numbers: List<Int>, rules: Rules): Boolean {
        numbers.indices.forEach { i ->
            val numbersBefore = numbers.subList(0, i)
            val number = numbers[i]
            val numbersAfter = numbers.subList(i + 1, numbers.size)

            val mustBeBefore = rules.mustBeBefore[number].orEmpty()
            val beforeOk = numbersBefore.all { it in mustBeBefore }
            if (!beforeOk) return false

            val mustBeAfter = rules.mustBeAfter[number].orEmpty()
            val afterOk = numbersAfter.all { it in mustBeAfter }
            if (!afterOk) return false
        }
        return true
    }

    private fun parse(input: List<String>): Pair<Rules, List<List<Int>>> {
        val (rawRules, rawNumbers) = input
            .partition { it.contains("|") }

        val rules = parseRules(rawRules)
        val numbers = rawNumbers
            .filter { it.isNotEmpty() }
            .map { it.split(",").map(String::toInt) }

        return rules to numbers
    }

    private fun parseRules(input: List<String>): Rules {
        val mustBeAfter = mutableMapOf<Int, List<Int>>()
        val mustBeBefore = mutableMapOf<Int, List<Int>>()

        input.forEach {
            val (l, r) = it.split("|").map(String::toInt)
            mustBeAfter.compute(l) { _, list -> list.orEmpty() + r }
            mustBeBefore.compute(r) { _, list -> list.orEmpty() + l }
        }

        return Rules(mustBeAfter, mustBeBefore)
    }

    data class Rules(
        val mustBeAfter: Map<Int, List<Int>>,
        val mustBeBefore: Map<Int, List<Int>>,
    )
}
