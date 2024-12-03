package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.PrintColor
import ch.ranil.aoc.printColor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day03 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(161, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(170778545, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(48, compute2(test2Input))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(82868252, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input
            .sumOf { calculate1(it) }
    }

    private val regex1 = "mul\\(([0-9]{1,3}),([0-9]{1,3})\\)".toRegex()
    private fun calculate1(input: String): Int {
        val matches = regex1.findAll(input).toList()
        return matches.sumOf { matchResult ->
            val (_, i1, i2) = matchResult.groupValues
            i1.toInt() * i2.toInt()
        }
    }

    private fun compute2(input: List<String>): Long {
        val memory = input.joinToString(separator = "")
        return calculate2(memory)
    }

    private val regex2 = "(mul\\(([0-9]{1,3}),([0-9]{1,3})\\)|(do\\(\\))|(don't\\(\\)))".toRegex()
    private fun calculate2(input: String): Long {
        val matches = regex2.findAll(input).toList()
        var enabled = true
        var sum = 0L
        for (match in matches) {
            val op = match.groupValues.first()

            if (enabled) {
                printColor(PrintColor.GREEN,"$op ")
            } else {
                printColor(PrintColor.RED,"$op ")
            }

            if (op.startsWith("mul") && enabled) {
                sum += calculate(match)
            } else if (op == "do()") {
                enabled = true
            } else if (op == "don't()") {
                enabled = false
            }
        }

        return sum
    }

    private fun calculate(matchResult: MatchResult): Long {
        val (_, _, i1, i2) = matchResult.groupValues
        return i1.toLong() * i2.toLong()
    }
}
