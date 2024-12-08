package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.math.max
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
        assertEquals(2286, compute2(testInput))
        assertEquals(59795, compute2(puzzleInput))
    }

    @Test
    fun part2Dummy() {
        assertEquals(48, "Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green".parseGame().second.determinePower())
    }

    private fun compute1(input: List<String>): Int {
        return input
            .map { it.parseGame() }
            .filterNot { (_, subsets) ->
                subsets.any { subset ->
                    subset.any { (count, cube) ->
                        availableColours.getValue(cube) < count
                    }
                }
            }
            .sumOf { it.first }
    }

    private fun compute2(input: List<String>): Int {
        return input
            .map { it.parseGame() }
            .sumOf { (_, subsets) ->
                subsets.determinePower()
            }
    }

    private fun List<List<Pair<Int, String>>>.determinePower(): Int {
        val minPowerMap = mutableMapOf<String, Int>()
        forEach { subset ->
            subset.forEach { (count, cube) ->
                val curVal = minPowerMap[cube] ?: 0
                minPowerMap[cube] = max(curVal, count)
            }
        }
        return minPowerMap.values.reduce { a, b -> a * b }
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

    private val availableColours = mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14,
    )
}