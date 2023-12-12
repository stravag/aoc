package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.math.abs
import kotlin.test.assertEquals

class Day12 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(0, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(0, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(1, countArrangements("???.### 1,1,3"))
        assertEquals(4, countArrangements(".??..??...?##. 1,1,3"))
        assertEquals(1, countArrangements("?#?#?#?#?#?#?#? 1,3,1,6"))
        assertEquals(1, countArrangements("????.#...#... 4,1,1"))
        assertEquals(4, countArrangements("????.######..#####. 1,6,5"))
        assertEquals(10, countArrangements("?###???????? 3,2,1"))
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
        val (puzzle, groupCounts) = parse(s)
        return 0
    }

    private fun getGroupCounts(puzzle: String): List<Int> {
        puzzle
        return emptyList()
    }

    private fun parse(s: String): Pair<String, List<Int>> {
        val (puzzle, groups) = s.split(" ")
        val groupCounts = groups.split(",").map { it.toInt() }

        return puzzle to groupCounts
    }
}
