package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day01 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(3, compute1(testInput))
        assertEquals(1135, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(6, compute2(testInput))
        assertEquals(6558, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        var count = 0
        var position = 50
        input.forEach {
            val steps = it.drop(1).toInt()
            position = if (it.first() == 'L') {
                position - steps
            } else {
                position + steps
            } % 100

            if (position == 0) count++
        }
        return count
    }

    private fun compute2(input: List<String>): Int {
        var count = 0
        var position = 50

        input.forEach { line ->
            val n = line.drop(1).toInt()
            val turns = n % 100
            val newPosition: Int
            count += n / 100
            if (line.first() == 'L') {
                newPosition = (position - turns + 100) % 100
                if ((newPosition > position || newPosition == 0) && position != 0) count++
            } else {
                newPosition = (position + turns) % 100
                if (newPosition < position) count++
            }
            position = newPosition
        }
        return count
    }

}