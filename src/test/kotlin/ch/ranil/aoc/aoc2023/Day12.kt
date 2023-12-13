package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
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
    @Ignore("brute-forced")
    fun part1Puzzle() {
        assertEquals(7169, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(1, countArrangementsOptimized("???.### 1,1,3".unfold()))
        assertEquals(16384, countArrangementsOptimized(".??..??...?##. 1,1,3".unfold()))
        assertEquals(1, countArrangementsOptimized("?#?#?#?#?#?#?#? 1,3,1,6".unfold()))
        assertEquals(16, countArrangementsOptimized("????.#...#... 4,1,1".unfold()))
        assertEquals(2500, countArrangementsOptimized("????.######..#####. 1,6,5".unfold()))
        assertEquals(506250, countArrangementsOptimized("?###???????? 3,2,1".unfold()))
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
        input.forEach {

        }
        return 0
    }

    private fun countArrangements(s: String): Int {
        val (puzzle, groupSizes) = parse(s)
        val puzzleArray = puzzle.toMutableList()
        val variablePositions = puzzle.mapIndexedNotNull { index, c ->
            if (c == '?') index else null
        }

        val bruteForcePermutations = 1 shl (variablePositions.size)
        var arrangements = 0
        println("$bruteForcePermutations permutations in... ")
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
        println("... $duration")
        return arrangements
    }

    private fun countArrangementsOptimized(s: String): Int {
        val (puzzle, groupSizes) = parse(s)
        val puzzleArray = puzzle.toMutableList()
        val dotsMissing = groupSizes.size - puzzle.split("\\.+".toRegex()).count { it.isNotBlank() }
        val possibleDotPositions = puzzle
            .mapIndexedNotNull { index, c ->
                if (c == '?') index else null
            }
            .filter { variablePos ->
                if (dotsMissing > 0) {
                    val precededByDot = puzzle.getOrNull(variablePos - 1) == '.'
                    val followedByDot = puzzle.getOrNull(variablePos + 1) == '.'
                    !precededByDot && !followedByDot && variablePos != 0 && variablePos != (puzzle.length - 1)
                } else {
                    // if the groups are already split correctly any variable could be a dot
                    true
                }
            }

        /**
         * better solution:
         * - always have at least `dotCountMissing` dots
         * - mandatory dots need at least a gap of 1 character
         */
        val bruteForcePermutations = 1 shl (possibleDotPositions.size - 1)
        var arrangements = 0
        println("$bruteForcePermutations permutations in... ")
        val (_, duration) = measureTimedValue {
            repeat(bruteForcePermutations) { iteration ->
                val variableAttempt = iteration.toDotHashRepresentation(possibleDotPositions.size)
                possibleDotPositions.forEachIndexed { attemptPosition, puzzleIndex ->
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

    private fun Int.toDotHashRepresentation(size: Int): String {
        val binaryString = Integer.toBinaryString(this).padStart(size, '0')
        val springString = binaryString
            .replace('1', '.')
            .replace('0', '#')
        return springString
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
