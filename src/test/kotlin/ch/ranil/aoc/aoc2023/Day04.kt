package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.math.pow
import kotlin.test.assertEquals

class Day04 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(13, compute1(testInput))
        assertEquals(26218, compute1(puzzleInput))
    }

    @Test
    fun part2() {
        assertEquals(0, compute2(testInput))
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input.map { line ->
            val (gamePart, numbersPart) = line.split(":")
            val (winningPart, availablePart) = numbersPart.split("|")
            ScratchCard(
                num = gamePart
                    .split(" ")
                    .filter { it.isNotBlank() }[1]
                    .toInt(),
                winning = winningPart
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() },
                avail = availablePart
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .map { it.toInt() },
            )
        }.sumOf {
            it.getPoints()
        }
    }

    private fun compute2(input: List<String>): Int {
        return input.size
    }

    data class ScratchCard(
        val num: Int,
        val winning: List<Int>,
        val avail: List<Int>,
    ) {
        fun getPoints(): Int {
            val matchingNumbers = winning.intersect(avail.toSet())
            return if (matchingNumbers.isEmpty()) {
                0
            } else {
                2.0.pow(matchingNumbers.size.toDouble() - 1).toInt()
            }
        }
    }

    @Test
    fun pointTest() {
        assertEquals(0, ScratchCard(0, listOf(0), listOf(1)).getPoints())
        assertEquals(1, ScratchCard(0, listOf(1), listOf(1)).getPoints())
        assertEquals(2, ScratchCard(0, listOf(1, 2), listOf(1, 2)).getPoints())
        assertEquals(4, ScratchCard(0, listOf(1, 2, 3), listOf(1, 2, 3)).getPoints())
        assertEquals(8, ScratchCard(0, listOf(1, 2, 3, 4), listOf(1, 2, 3, 4)).getPoints())
    }
}