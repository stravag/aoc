package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.types.Point
import ch.ranil.aoc.aoc2023.Day16.Direction.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day16 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(46, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(6361, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(51, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(6701, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val initialBeam = Beam(Point(0, 0), E)
        return countEnergizedTiles(initialBeam, input)
    }

    private fun compute2(input: List<String>): Int {
        val xMax = input.first().count() - 1
        val yMax = input.count() - 1

        val initialBeams = (0..xMax).map { x -> Beam(Point(x, 0), S) } +
                (0..yMax).map { y -> Beam(Point(0, y), E) } +
                (0..yMax).map { y -> Beam(Point(xMax, y), W) } +
                (0..xMax).map { x -> Beam(Point(x, yMax), N) }

        return initialBeams.maxOf { countEnergizedTiles(it, input) }
    }

    private fun countEnergizedTiles(initialBeam: Beam, input: List<String>): Int {
        var activeBeams: Set<Beam> = setOf(initialBeam)
        val beamCache: MutableSet<Beam> = mutableSetOf(initialBeam)
        val energized: MutableSet<Point> = mutableSetOf(initialBeam.p)

        do {
            activeBeams = activeBeams
                .flatMap { beam -> beam.move(input.getChar(beam.p)) }
                .filter { it.p.containedIn(input) }
                .filterNot { beamCache.contains(it) }
                .onEach { energized.add(it.p) }
                .toSet()
        } while (beamCache.addAll(activeBeams))

        return energized.size
    }

    private infix fun Point.containedIn(map: List<String>): Boolean {
        return map.getOrNull(y)?.getOrNull(x) != null
    }

    private fun List<String>.getChar(p: Point): Char? {
        return this.getOrNull(p.y)?.getOrNull(p.x)
    }

    private data class Beam(
        val p: Point,
        val direction: Direction,
    ) {
        private fun move(direction: Direction): Beam = when (direction) {
            N -> Beam(p.north(), direction)
            E -> Beam(p.east(), direction)
            S -> Beam(p.south(), direction)
            W -> Beam(p.west(), direction)
        }

        fun move(char: Char?): Set<Beam> {
            return when (char) {
                '/' -> when (direction) {
                    N -> setOf(move(E))
                    E -> setOf(move(N))
                    S -> setOf(move(W))
                    W -> setOf(move(S))
                }

                '\\' -> when (direction) {
                    N -> setOf(move(W))
                    E -> setOf(move(S))
                    S -> setOf(move(E))
                    W -> setOf(move(N))
                }

                '-' -> when (direction) {
                    N, S -> setOf(move(E), move(W))
                    else -> setOf(move(direction))
                }

                '|' -> when (direction) {
                    E, W -> setOf(move(N), move(S))
                    else -> setOf(move(direction))
                }

                '.' -> setOf(move(direction))
                else -> emptySet()
            }
        }
    }

    private enum class Direction {
        N, E, S, W
    }
}
