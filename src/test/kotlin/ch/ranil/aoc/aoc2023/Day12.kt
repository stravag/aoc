package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.PrintColor
import ch.ranil.aoc.common.printlnColor
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
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
    fun part1TestWip() {
        //                             ##.###.#.######
        countArrangementsOptimized("?#?#?#?#?#?#?#? 1,3,1,6")
        countArrangementsOptimized("???.### 1,1,3")
        countArrangementsOptimized(".??..??...?##. 1,1,3")
        countArrangementsOptimized("????.#...#... 4,1,1")
        countArrangementsOptimized("????.######..#####. 1,6,5")
        countArrangementsOptimized("?###???????? 3,2,1")
    }

    @Test
    fun part1Puzzle() {
        assertEquals(7169, compute1(puzzleInput))
    }

    @Test
    @Disabled
    fun part2Test() {
        assertEquals(16384, countArrangements(".??..??...?##. 1,1,3".unfold()))
        assertEquals(1, countArrangements("???.### 1,1,3".unfold()))
        assertEquals(1, countArrangements("?#?#?#?#?#?#?#? 1,3,1,6".unfold()))
        assertEquals(16, countArrangements("????.#...#... 4,1,1".unfold()))
        assertEquals(2500, countArrangements("????.######..#####. 1,6,5".unfold()))
        assertEquals(506250, countArrangements("?###???????? 3,2,1".unfold()))
    }

    @Test
    fun part2Test2Wip() {
        // from puzzle
        countArrangementsOptimized("??#..#???#? 1,1,4".unfold())
        countArrangementsOptimized("?.???#???. 2,2".unfold())
        countArrangementsOptimized("??#.????#???.#?# 1,5,1,1,1".unfold())

        countArrangementsOptimized(".??..??...?##. 1,1,3".unfold())
        countArrangementsOptimized("?###???????? 3,2,1".unfold())
        countArrangementsOptimized("???.### 1,1,3".unfold())
        countArrangementsOptimized("?#?#?#?#?#?#?#? 1,3,1,6".unfold())
        countArrangementsOptimized("????.#...#... 4,1,1".unfold())
        countArrangementsOptimized("????.######..#####. 1,6,5".unfold())

    }

    @Test
    fun isSplit() {
        assertTrue("#.?".toList().isSplit())
        assertTrue("#.#".toList().isSplit())
        assertFalse("..?".toList().isSplit())
        assertFalse("?.?".toList().isSplit())
        assertFalse("???".toList().isSplit())
        assertFalse("###".toList().isSplit())
        assertFalse(".##".toList().isSplit())
        assertFalse("?.#".toList().isSplit())
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
        input
            .map { it.unfold() }
            .map { countArrangementsOptimized(it) }

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

    private class State(
        var groupCounter: Int = 0,
        var offset: Int = 0,
        puzzle: String
    ) {
        val puzzleCopy = puzzle.toMutableList()
    }

    private fun countArrangementsOptimized(s: String): Int {
        val (puzzle, groupSizes) = parse(s)

        val hashesNeeded = groupSizes.sum()
        val minDotsNeeded = groupSizes.size - 1
        val wiggleRoom = puzzle.length - hashesNeeded - minDotsNeeded

        println("Variable spots: ${puzzle.length - hashesNeeded - minDotsNeeded} / $groupSizes")

        return if (wiggleRoom == 0) {
            1
        } else {
            // find valid position
            val puzzleCopy = puzzle.toMutableList()
            var offset = 0
            var lastOffsetDiff = 0
            println("S: ${puzzleCopy.joinToString("")}\n")

            var groupCounter = 0
            while (groupCounter < groupSizes.size) {
                val groupSize = groupSizes[groupCounter]
                for (j in offset..<puzzleCopy.size) {
                    val potentialGroup = puzzleCopy.subList(j, j + groupSize)
                    val remainingPart = puzzleCopy.subList(j + groupSize, puzzleCopy.size)
                    if (potentialGroup.couldFit(groupSize)
                        && remainingPart.firstOrNull().canBeDot()
                        && countGroups(remainingPart) < groupSizes.size - groupCounter
                    ) {
                        for (k in 0..<groupSize) {
                            puzzleCopy[k + offset] = '#'
                        }
                        if (remainingPart.isNotEmpty()) puzzleCopy[offset + groupSize] = '.'
                        lastOffsetDiff = groupSize + 1
                        offset += lastOffsetDiff
                        groupCounter++
                        break
                    } else if (potentialGroup.isSplit()
                        || countGroups(remainingPart) > groupSizes.size - groupCounter
                        || hashCount(potentialGroup, remainingPart.firstOrNull()) > groupSize
//                        || (potentialGroup.couldFit(groupSize) && remainingPart.firstOrNull() == '#')
                    ) {
                        // reached dead end, revert last group placing
                        println("REVERT!")
                        repeat(lastOffsetDiff) {
                            puzzleCopy[offset - it - 1] = puzzle[offset - it - 1]
                        }
                        offset -= lastOffsetDiff
                        groupCounter--
                        offset++
                        break
                    } else {
                        offset++
//                        lastOffsetDiff++
                    }
                }
                println("$groupSize: ${puzzleCopy.joinToString("")}")
            }
            require(isValidArrangement(puzzleCopy, groupSizes, true))
            0
        }
    }

    private fun List<Char>.couldFit(groupSize: Int): Boolean {
        require(size == groupSize) { "Unexpected size of list: $size instead of $groupSize" }
        return all { it == '#' || it == '?' }
    }

    private fun List<Char>.isSplit(): Boolean {
        if (!this.contains('.')) return false
        return this
            .takeWhile { it != '.' }
            .any { it == '#' }
    }

    private fun countGroups(l: List<Char>): Int {
        return l.joinToString("")
            .split(".")
            .count { it.contains("#") }
    }

    private fun Char?.canBeDot(): Boolean {
        return this == '?' || this == '.' || this == null
    }

    private fun hashCount(l: List<Char>, vararg cs: Char?): Int {
        if (l.first() == '#') {

        }
        return l.count { it == '#' } + cs.count { it == '#' }
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

    private fun isValidArrangement(puzzle: List<Char>, groupSizes: List<Int>, print: Boolean = false): Boolean {
        val puzzleString = puzzle.joinToString("").replace("?", ".")
        val sizes = puzzleString
            .split("\\.+".toRegex())
            .filter { it.isNotBlank() }
            .map { it.length }

        val isValidArrangement = sizes == groupSizes
        if (print) {
            if (isValidArrangement) {
                printlnColor(PrintColor.GREEN, puzzleString)
            } else {
                printlnColor(PrintColor.RED, puzzleString)
            }
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
