package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printColor
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

    private fun compute1(input: List<String>): Long {
        val memory = input.joinToString(separator = "")
        val regex = "mul\\([0-9]{1,3},[0-9]{1,3}\\)".toRegex()
        val matches = regex.findAll(memory).map { it.value }
        return matches.sumOf { calculate(it) }
    }

    private fun compute2(input: List<String>): Long {
        val memory = input.joinToString(separator = "")
        val regex = "(mul\\([0-9]{1,3},[0-9]{1,3}\\)|(do\\(\\))|(don't\\(\\)))".toRegex()
        val matches = regex.findAll(memory).toList()
        var enabled = true
        var sum = 0L
        for (match in matches) {
            val op = match.groupValues.first()

            if (enabled) {
                printColor("$op ", PrintColor.GREEN)
            } else {
                printColor("$op ", PrintColor.RED)
            }

            if (op.startsWith("mul") && enabled) {
                sum += calculate(op)
            } else if (op == "do()") {
                enabled = true
            } else if (op == "don't()") {
                enabled = false
            }
        }
        return sum
    }

    private fun calculate(mul: String): Long {
        val mulRegex = "mul\\(([0-9]{1,3}),([0-9]{1,3})\\)".toRegex()
        val (_, i1, i2) = mulRegex.find(mul)!!.groupValues
        return i1.toLong() * i2.toLong()
    }
}
