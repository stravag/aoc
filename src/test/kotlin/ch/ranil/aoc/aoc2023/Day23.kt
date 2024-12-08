package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.types.Point
import ch.ranil.aoc.common.charForPoint
import ch.ranil.aoc.common.containsPoint
import org.junit.jupiter.api.Test
import kotlin.math.max
import kotlin.test.assertEquals

class Day23 : AbstractDay() {

    @Test
    fun part1Simple() {
        assertEquals(
            11, compute1(
                """
            #.#####
            #.>...#
            #v###.#
            #.#...#
            #...#.#
            #####.#
        """.trimIndent().lines()
            )
        )

        assertEquals(
            4, compute1(
                """
            #.###
            #.>.#
            ###.#
        """.trimIndent().lines()
            )
        )

        assertEquals(
            3, compute1(
                """
            #.##
            #..#
            ##.#
        """.trimIndent().lines()
            )
        )
    }

    @Test
    fun part1Test() {
        assertEquals(94, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(2206, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        var best = 0
        val visited = mutableSetOf<Point>()
        val start = Point(1, 0)
        val end = Point(input.first().length - 2, input.size - 1)

        fun dfs(p: Point, steps: Int): Int {
            if (p == end) {
                best = max(steps, best)
                return best
            }
            visited += p
            neighborsOf(p, input)
                .map { it to 1 }
                .filter { (place, _) -> place !in visited }
                .forEach { (place, distance) -> dfs(place, distance + steps) }
            visited -= p
            return best
        }

        return dfs(start, 0)
    }

    private fun neighborsOf(p: Point, input: List<String>): List<Point> {
        fun next(c1: () -> Point, matchingSlope: Char): Point? {
            val nxt = c1()
            val char = input.charForPoint(nxt)
            return if (input.containsPoint(nxt) && (char == matchingSlope || char == '.')) {
                nxt
            } else {
                null
            }
        }

        return listOfNotNull(
            next(p::north, '^'),
            next(p::east, '>'),
            next(p::south, 'v'),
            next(p::west, '<'),
        )
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }
}
