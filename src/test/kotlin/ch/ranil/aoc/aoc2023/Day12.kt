package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.time.measureTimedValue

class Day12 : AbstractDay() {

    @Test
    fun part1Test() {
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
        assertEquals(0, compute1(testInput))
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
        return 0
    }

    private fun countArrangements(s: String): Int {
        val (puzzle, groupSizes) = parse(s)
        val puzzleArray = puzzle.toMutableList()
        val variablePositions = puzzle.mapIndexedNotNull { index, c ->
            if (c == '?') index else null
        }

        /**
         * Brute force attempt, better solution:
         * - always have at least `dotCountMissing` dots
         * - mandatory dots need at least a gap of 1 character
         */
        val bruteForcePermutations = 1 shl (variablePositions.size)
        var arrangements = 0
        val (_, duration) = measureTimedValue {
            repeat(bruteForcePermutations) { iteration ->
                val variableAttempt = iteration.toDotHashRepresentation(variablePositions.size)
                variablePositions.forEachIndexed { attemptPosition, puzzleIndex ->
                    puzzleArray[puzzleIndex] = variableAttempt[attemptPosition]
                }
                if (isValidArrangement(puzzleArray, groupSizes)) {
                    arrangements++
                }
            }
        }
        println("$bruteForcePermutations permutations in $duration")
        return arrangements
    }

    private fun isValidArrangement(puzzle: List<Char>, groupSizes: List<Int>): Boolean {
        val puzzleString = puzzle.joinToString("")
        val sizes = puzzleString
            .split("\\.+".toRegex())
            .filter { it.isNotBlank() }
            .map { it.length }

        return sizes == groupSizes
    }

    private fun Int.toDotHashRepresentation(size: Int): String {
        return Integer.toBinaryString(this)
            .replace('1', '.')
            .replace('0', '#')
            .padStart(size, '#')
    }

    private fun parse(s: String): Pair<String, List<Int>> {
        val (puzzle, groups) = s.split(" ")
        val groupSizes = groups.split(",").map { it.toInt() }

        return puzzle to groupSizes
    }
}
