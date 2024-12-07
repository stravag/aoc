package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day07 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(3749, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(8401132154762, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(11387, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(95297119227552, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return input
            .map { line ->
                val (expectedResult, numbers) = parse(line)
                val hasResult = hasResult(expectedResult, numbers, listOf(Long::plus, Long::times))
                expectedResult to hasResult
            }
            .filter { (_, hasResult) -> hasResult }
            .sumOf { (expectedResult, _) -> expectedResult }
    }

    private fun compute2(input: List<String>): Long {
        return input
            .map { line ->
                val (expectedResult, numbers) = parse(line)
                val hasResult =
                    hasResult(expectedResult, numbers, listOf(Long::plus, Long::times, { l1, l2 -> "$l1$l2".toLong() }))
                expectedResult to hasResult
            }
            .filter { (_, hasResult) -> hasResult }
            .sumOf { (expectedResult, _) -> expectedResult }
    }

    private fun hasResult(expectedResult: Long, numbers: List<Long>, operations: List<(Long, Long) -> Long>): Boolean {
        return hasResultRec(
            expectedResult = expectedResult,
            numbers = numbers,
            operations = operations,
            currentResult = numbers.first(),
            i = 1
        )
    }

    private fun hasResultRec(
        expectedResult: Long,
        numbers: List<Long>,
        operations: List<(Long, Long) -> Long>,
        currentResult: Long,
        i: Int
    ): Boolean {
        if (currentResult > expectedResult) return false // early abort
        if (i == numbers.size && currentResult != expectedResult) return false // end reached, no result
        if (i == numbers.size) return true // valid result found

        val nextNum = numbers[i]
        operations.forEach { operation ->
            val hasResult = hasResultRec(
                expectedResult = expectedResult,
                numbers = numbers,
                operations = operations,
                currentResult = operation(currentResult, nextNum),
                i = i + 1,
            )
            if (hasResult) return true
        }

        return false
    }

    private fun parse(input: String): Pair<Long, List<Long>> {
        val (result, numberStrings) = input.split(":")
        val numbers = numberStrings
            .split(" ")
            .filter { it.isNotBlank() }
            .map(String::toLong)
        return result.toLong() to numbers
    }
}
