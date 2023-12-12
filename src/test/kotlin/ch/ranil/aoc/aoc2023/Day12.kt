package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.assertEquals

class Day12 : AbstractDay() {

    @Test
    fun part1Simple() {
        assertEquals(4, countArrangements(".??..??...?##. 1,1,3"))
        assertEquals(1, countArrangements("????.#...#... 4,1,1"))
        assertEquals(4, countArrangements("????.######..#####. 1,6,5"))
    }

    @Test
    fun part1Complex() {
        assertEquals(1, countArrangements("???.### 1,1,3"))
        assertEquals(4, countArrangements(".??..??...?##. 1,1,3"))
        assertEquals(1, countArrangements("?#?#?#?#?#?#?#? 1,3,1,6"))
        assertEquals(1, countArrangements("????.#...#... 4,1,1"))
        assertEquals(4, countArrangements("????.######..#####. 1,6,5"))
        assertEquals(10, countArrangements("?###???????? 3,2,1"))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute1(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Long {
        return 0
    }

    private fun compute2(input: List<String>): Long {
        return 0
    }

    private fun countArrangements(s: String): Int {
        val (puzzle, groupSizes) = parse(s)
        val knownPuzzleGroups = puzzle.split("\\.+".toRegex()).filter { it.isNotBlank() }

        // simple case
        if (knownPuzzleGroups.size == groupSizes.size) {
            var arrangements = 1
            for (i in groupSizes.indices) {
                val groupSize = groupSizes[i]
                val group = knownPuzzleGroups[i]
                val fixedGroupSize = group.count { it == '#' }
                val variableGroupSize = group.length - fixedGroupSize
                if (variableGroupSize > 0) {
                    val groupArrangements = variableGroupSize / (groupSize - fixedGroupSize)
                    arrangements *= groupArrangements
                }
            }
            return arrangements
        }

        return 0
    }

    private fun parse(s: String): Pair<String, List<Int>> {
        val (puzzle, groups) = s.split(" ")
        val groupSizes = groups.split(",").map { it.toInt() }

        return puzzle to groupSizes
    }
}
