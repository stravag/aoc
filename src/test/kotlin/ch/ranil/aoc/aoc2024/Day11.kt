package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.isEven
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day11 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(55312, compute("125 17", 25))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(203609, compute("5 89749 6061 43 867 1965860 0 206250", 25))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(7, compute("125", 6))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(240954878211138, compute("5 89749 6061 43 867 1965860 0 206250", 75))
    }


    private fun compute(input: String, iterations: Int): Long {
        val numbers = input.split(" ")
            .map { it.trim().toLong() }

        val cache = mutableMapOf<Pair<Long, Int>, Long>()
        val sum = numbers.sumOf { drillDown(it, 0, iterations, cache) }

        return sum
    }

    private fun drillDown(number: Long, i: Int, iterations: Int, cache: MutableMap<Pair<Long, Int>, Long>): Long {
        val key = number to i

        if (cache.contains(key)) return cache.getValue(key)
        if (i == iterations) return 1

        return applyRules(number)
            .sumOf { drillDown(it, i + 1, iterations, cache) }
            .also { cache[key] = it }
    }

    private fun applyRules(num: Long): List<Long> {
        val s = num.toString()
        val l = s.length
        when {
            num == 0L -> return listOf(1L)
            l.isEven() -> {
                val left = s.subSequence(0, l / 2).toString().toLong()
                val right = s.subSequence(l / 2, l).toString().toLong()
                return listOf(left, right)
            }

            else -> return listOf(num * 2024)
        }
    }
}
