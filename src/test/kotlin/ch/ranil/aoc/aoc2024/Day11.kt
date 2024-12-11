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
    fun part2Puzzle() {
        assertEquals(240954878211138, compute("5 89749 6061 43 867 1965860 0 206250", 75))
    }

    private fun compute(input: String, blinks: Int): Long {
        val stones = input.split(" ").map { it.toLong() }
        val cache = mutableMapOf<Pair<Long, Int>, Long>()
        return stones.sumOf { drillDown(it, 0, blinks, cache) }
    }

    private fun drillDown(stone: Long, i: Int, blinks: Int, cache: MutableMap<Pair<Long, Int>, Long>): Long {
        val key = stone to i

        if (key in cache) return cache.getValue(key)
        if (i == blinks) return 1

        return blink(stone)
            .sumOf { nextStone -> drillDown(nextStone, i + 1, blinks, cache) }
            .also { stoneCount -> cache[key] = stoneCount }
    }

    private fun blink(num: Long): List<Long> {
        val s = num.toString()
        val l = s.length
        return when {
            num == 0L -> listOf(1L)
            l.isEven() -> split(s)
            else -> listOf(num * 2024)
        }
    }

    private fun split(s: String): List<Long> {
        val left = s.subSequence(0, s.length / 2).toString().toLong()
        val right = s.subSequence(s.length / 2, s.length).toString().toLong()
        return listOf(left, right)
    }
}
