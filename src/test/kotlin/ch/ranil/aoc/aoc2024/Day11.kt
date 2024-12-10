package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.Debug.debug
import ch.ranil.aoc.common.isEven
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day11 : AbstractDay() {

    @Test
    fun part1RulesTest() {
        Debug.enable()

        assertEquals(listOf(1L), applyRules(0))
        assertEquals(listOf(10L, 0L), applyRules(1000))
        assertEquals(listOf(11L, 22L), applyRules(1122))
        assertEquals(listOf(4048L), applyRules(2))
    }

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(55312, compute1("125 17"))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(203609, compute1("5 89749 6061 43 867 1965860 0 206250"))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(""))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2("5 89749 6061 43 867 1965860 0 206250"))
    }

    private fun compute1(input: String): Long {
        var numbers = input.split(" ")
            .map { it.trim().toLong() }

        repeat(25) {
            debug { numbers.joinToString(separator = " ") }
            numbers = numbers.flatMap { applyRules(it) }
        }

        return numbers.size.toLong()
    }

    private fun applyRules(num: Long): List<Long> {
        val numString = num.toString()
        when {
            num == 0L -> return listOf(1L)
            numString.length.isEven() -> {
                val left = numString.subSequence(0, numString.length / 2).toString().toLong()
                val right = numString.subSequence(numString.length / 2, numString.length).toString().toLong()
                return listOf(left, right)
            }

            else -> return listOf(num * 2024)
        }
    }

    private fun compute2(input: String): Long {
        TODO()
    }
}
