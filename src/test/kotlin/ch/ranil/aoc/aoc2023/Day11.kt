package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import org.junit.jupiter.api.Test
import kotlin.math.abs
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
        assertEquals(1030, compute2(testInput, offSet = 10 - 1))
        assertEquals(8410, compute2(testInput, offSet = 100 - 1))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(702770569197, compute2(puzzleInput, offSet = 1000000 - 1))
    }

    private fun compute1(input: List<String>): Long {
        val galaxyDistances = getGalaxyDistances(input, offSet = 1)

        return galaxyDistances.values.sum()
    }

    private fun compute2(input: List<String>, offSet: Long): Long {
        val galaxyDistances = getGalaxyDistances(input, offSet)
        return galaxyDistances.values.sum()
    }

    private fun getGalaxyDistances(input: List<String>, offSet: Long): MutableMap<GalaxyPair, Long> {
        var xShift = 0L
        var yShift = 0L
        val galaxies = mutableListOf<Galaxy>()
        input.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                if (c == '#') galaxies.add(Galaxy(galaxies.size + 1, BigPoint(x + xShift, y + yShift)))
                if (input.isColumnBlank(x)) xShift += offSet // empty column, shift to right
            }
            if (s.isEmptySpace()) yShift += offSet // empty line, shift down
            xShift = 0 // new line reset x shift
        }

        val galaxyDistances = mutableMapOf<GalaxyPair, Long>()
        for (galaxy in galaxies) {
            val otherGalaxies = galaxies - galaxy
            for (otherGalaxy in otherGalaxies) {
                if (!galaxyDistances.contains(GalaxyPair(galaxy, otherGalaxy))) {
                    val dist = galaxy.pos.distanceTo(otherGalaxy.pos)
                    galaxyDistances[GalaxyPair(galaxy, otherGalaxy)] = dist
                }
            }
        }
        return galaxyDistances
    }

    private fun List<String>.isColumnBlank(x: Int): Boolean {
        return this.map { it[x] }.none { it == '#' }
    }

    private fun String.isEmptySpace(): Boolean {
        return this.none { it == '#' }
    }

    private data class Galaxy(
        val num: Int,
        val pos: BigPoint,
    )

    private data class GalaxyPair(
        val galaxies: Set<Galaxy>,
    ) {
        constructor(a: Galaxy, b: Galaxy) : this(setOf(a, b))

        override fun toString(): String {
            return galaxies.joinToString("<->") { it.num.toString() }
        }
    }

    private data class BigPoint(val x: Long, val y: Long)

    private fun BigPoint.distanceTo(other: BigPoint): Long {
        return abs(other.x - x) + abs(other.y - y)
    }
}
