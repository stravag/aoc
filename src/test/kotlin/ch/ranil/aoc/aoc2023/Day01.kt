package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day01 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(142, compute1(testInput))
        assertEquals(55488, compute1(puzzleInput))
    }

    @Test
    fun part2Dummy() {
        assertEquals(83, "eightwothree".convertNumberStrings())
        assertEquals(42, "4nineeightseven2".convertNumberStrings())
    }

    @Test
    fun part2() {
        assertEquals(281, compute2(test2Input))
        assertEquals(55614, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input.sumOf { line ->
            val numbersInLine = line.toCharArray().filter { it.isDigit() }
            "${numbersInLine.first()}${numbersInLine.last()}".toInt()
        }
    }

    private fun compute2(input: List<String>): Int {
        return input.sumOf { line ->
            val convertedLine = line.convertNumberStrings()
            convertedLine
        }
    }

    private fun String.convertNumberStrings(): Int {
        val line = this
        val map = mapOf(
            "one" to 1,
            "two" to 2,
            "three" to 3,
            "four" to 4,
            "five" to 5,
            "six" to 6,
            "seven" to 7,
            "eight" to 8,
            "nine" to 9,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
            "6" to 6,
            "7" to 7,
            "8" to 8,
            "9" to 9,
        )

        val numbers = map.flatMap { (numberStr, number) ->
            setOf(
                line.indexOf(numberStr) to number,
                line.lastIndexOf(numberStr) to number,
            ).filter { it.first >= 0 }
        }.sortedBy { it.first }.map { it.second }

        return "${numbers.first()}${numbers.last()}".toInt()
    }


}