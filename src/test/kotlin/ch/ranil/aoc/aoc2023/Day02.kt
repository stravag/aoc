package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day02 : AbstractDay() {

    @Test
    fun part1() {
        assertEquals(8, compute1(testInput))
        assertEquals(2617, compute1(puzzleInput))
    }

    @Test
    fun part1Dummy() {
        assertEquals(6 to "blue", " 6 blue ".parseCube())
        assertEquals(listOf(3 to "blue", 4 to "red"), " 3 blue, 4 red".parseSubset())
        assertEquals(
            listOf(
                listOf(6 to "red", 1 to "blue", 3 to "green"),
                listOf(2 to "blue", 1 to "red", 2 to "green"),
            ), " 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green".parseSubsets()
        )
        assertEquals(
            5 to listOf(
                listOf(6 to "red", 1 to "blue", 3 to "green"),
                listOf(2 to "blue", 1 to "red", 2 to "green"),
            ), "Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green".parseGame()
        )
    }

    @Test
    fun part2() {
        assertEquals(0, compute2(testInput))
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input
            .map { it.parseGame() }
            .filterNot { (_, possibleSubsets) ->
                possibleSubsets.any { subset ->
                    subset.any { (count, cube) ->
                        val exceedsAvailability = availableColours.getValue(cube) < count
                        exceedsAvailability
                    }
                }
            }
            .sumOf { it.first }
    }

    private fun String.parseGame(): Pair<Int, List<List<Pair<Int, String>>>> {
        val (gameNum, cubes) = this
            .replace("Game ", "")
            .split(":")
        return gameNum.toInt() to cubes.parseSubsets()
    }

    private fun String.parseSubsets(): List<List<Pair<Int, String>>> {
        return this
            .trim()
            .split(";")
            .map { it.parseSubset() }
    }

    private fun String.parseSubset(): List<Pair<Int, String>> {
        return trim()
            .split(",")
            .map { cubeString ->
                cubeString.parseCube()
            }
    }

    private fun String.parseCube(): Pair<Int, String> {
        val (countStr, cubeStr) = this.trim().split(" ")
        return countStr.toInt() to cubeStr
    }

    val availableColours = mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14,
    )

    private fun compute2(input: List<String>): Int {
        return input.size
    }
}