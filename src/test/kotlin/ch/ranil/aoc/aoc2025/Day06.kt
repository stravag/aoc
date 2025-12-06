package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day06 : AbstractDay() {

    @Test
    fun part1() {
        Debug.enable()
        assertEquals(4277556, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(3261038365331, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        Debug.enable()
        assertEquals(3263827, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(8342588849093, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        val numbers = input
            .dropLast(1)
            .map { row ->
                row
                    .split(" ")
                    .mapNotNull { it.toLongOrNull() }
            }

        return input
            .last()
            .split(" ")
            .filter { it.isNotBlank() }
            .mapIndexed { i, operand ->
                val result = when (operand) {
                    "*" -> numbers.map { it[i] }.reduce(Long::times)
                    "+" -> numbers.map { it[i] }.reduce(Long::plus)
                    else -> error("Unknown operand: $operand")
                }
                Debug.debug { println("$i = $result") }
                result
            }
            .sum()
    }

    private fun compute2(input: List<String>): Long {
        val numbers = input
            .dropLast(1)
            .map { row -> row.map { it } }

        var result = 0L
        val currentNumbers = mutableListOf<Long>()
        for (rowIdx in (-1 until numbers.first().size).reversed()) {
            val number = numbers.map { it.getOrNull(rowIdx) }.joinToString("").trim().toLongOrNull()
            if (number == null) {
                val op = input.last()[rowIdx + 1]
                val r = when (op) {
                    '*' -> currentNumbers.reduce(Long::times)
                    '+' -> currentNumbers.reduce(Long::plus)
                    else -> error("Unknown operand: $op")
                }
                Debug.debug {
                    println("${currentNumbers.joinToString(" $op ")} = $r")
                }
                result += r
                currentNumbers.clear()
            } else {
                currentNumbers.add(number)
            }
        }

        return result
    }
}