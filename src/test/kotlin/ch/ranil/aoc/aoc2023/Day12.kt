package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.measureTimedValue

class Day12 : AbstractDay() {

    @Test
    fun part1TestOptimized() {
        assertEquals(1, countArrangements("???.### 1,1,3"))
        assertEquals(4, countArrangements(".??..??...?##. 1,1,3"))
        assertEquals(1, countArrangements("?#?#?#?#?#?#?#? 1,3,1,6"))
        assertEquals(1, countArrangements("????.#...#... 4,1,1"))
        assertEquals(4, countArrangements("????.######..#####. 1,6,5"))
        assertEquals(10, countArrangements("?###???????? 3,2,1"))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(7169, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(16384, countArrangements(".??..??...?##. 1,1,3".unfold()))
        assertEquals(1, countArrangements("???.### 1,1,3".unfold()))
        assertEquals(1, countArrangements("?#?#?#?#?#?#?#? 1,3,1,6".unfold()))
        assertEquals(16, countArrangements("????.#...#... 4,1,1".unfold()))
        assertEquals(2500, countArrangements("????.######..#####. 1,6,5".unfold()))
        assertEquals(506250, countArrangements("?###???????? 3,2,1".unfold()))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        return input.sumOf {
            countArrangements(it)
        }
    }

    private fun compute2(input: List<String>): Long {
        TODO()
    }

    private fun countArrangements(s: String): Int {
        val (puzzle, groupSizes) = parse(s)
        val puzzleArray = puzzle.toMutableList()

        val hashesMissing = groupSizes.sum() - puzzle.count { it == '#' }
        val variablePositions = puzzle.mapIndexedNotNull { index, c ->
            if (c == '?') index else null
        }

        var arrangements = 0
        val permutations = generatePermutations(variablePositions.size, hashesMissing)
        println("${permutations.size} permutations in... ")
        val (_, duration) = measureTimedValue {
            permutations.forEach { variableAttempt ->
                variablePositions.forEachIndexed { attemptPosition, puzzleIndex ->
                    puzzleArray[puzzleIndex] = variableAttempt[attemptPosition]
                }
                if (isValidArrangement(puzzleArray, groupSizes)) {
                    arrangements++
                }
            }
        }
        println("... $duration")
        return arrangements
    }

    private fun generatePermutations(numberOfVariables: Int, hashCount: Int): List<String> {
        val permutations = mutableListOf<String>()

        fun permutations(current: String, hashesLeft: Int, dotsLeft: Int) {
            if (current.length == numberOfVariables) {
                permutations.add(current)
                return
            }
            if (hashesLeft > 0) {
                permutations("$current#", hashesLeft - 1, dotsLeft)
            }
            if (dotsLeft > 0) {
                permutations("$current.", hashesLeft, dotsLeft - 1)
            }
        }

        permutations("", hashCount, numberOfVariables - hashCount)
        return permutations
    }

    private fun isValidArrangement(puzzle: List<Char>, groupSizes: List<Int>): Boolean {
        val puzzleString = puzzle.joinToString("").replace("?", "#")
        val sizes = puzzleString
            .split("\\.+".toRegex())
            .filter { it.isNotBlank() }
            .map { it.length }

        val isValidArrangement = sizes == groupSizes
        if (isValidArrangement) {
            // printlnColor(PrintColor.GREEN, puzzleString)
        } else {
            // printlnColor(PrintColor.RED, puzzleString)
        }
        return isValidArrangement
    }

    private fun parse(s: String): Pair<String, List<Int>> {
        val (puzzle, groups) = s.split(" ")
        val groupSizes = groups.split(",").map { it.toInt() }

        return puzzle to groupSizes
    }

    private fun String.unfold(): String {
        val (puzzle, groups) = this.split(" ")

        val unfoldedPuzzle = List(5) { puzzle }.joinToString("?")
        val unfoldedGroupSizes = List(5) { groups }.joinToString(",")
        return "$unfoldedPuzzle $unfoldedGroupSizes"
    }

}
