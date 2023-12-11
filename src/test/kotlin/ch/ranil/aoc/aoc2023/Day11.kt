package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day11 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(374, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(9974721, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(4, compute2(test2Input))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        var xShift = 0
        var yShift = 0
        val galaxies = mutableListOf<Galaxy>()
        input.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                if (c == '#') galaxies.add(Galaxy(galaxies.size + 1, Point(x + xShift, y + yShift)))
                if (input.isColumnBlank(x)) xShift++ // empty column, shift to right
            }
            if (s.isEmptySpace()) yShift++ // empty line, shift down
            xShift = 0 // new line reset x shift
        }

        val galaxyDistances = mutableMapOf<GalaxyPair, Int>()
        for (galaxy in galaxies) {
            val otherGalaxies = galaxies - galaxy
            for (otherGalaxy in otherGalaxies) {
                if (!galaxyDistances.contains(GalaxyPair(galaxy, otherGalaxy))) {
                    val dist = galaxy.pos.distanceTo(otherGalaxy.pos)
                    galaxyDistances[GalaxyPair(galaxy, otherGalaxy)] = dist
                }
            }
        }

        return galaxyDistances.values.sum()
    }

    private fun List<String>.isColumnBlank(x: Int): Boolean {
        return this.map { it[x] }.none { it == '#' }
    }

    private fun String.isEmptySpace(): Boolean {
        return this.none { it == '#' }
    }

    private fun compute2(input: List<String>): Int {
        return input.count()
    }

    private data class Galaxy(
        val num: Int,
        val pos: Point,
    )

    private data class GalaxyPair(
        val galaxies: Set<Galaxy>,
    ) {
        constructor(a: Galaxy, b: Galaxy) : this(setOf(a, b))

        override fun toString(): String {
            return galaxies.joinToString("<->") { it.num.toString() }
        }
    }

    private fun Collection<GalaxyPair>.flatten(): List<Galaxy> = flatMap { it.galaxies }
}
