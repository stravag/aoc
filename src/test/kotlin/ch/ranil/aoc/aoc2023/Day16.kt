package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
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
        assertEquals(0, compute1(puzzleInput))
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
        var activeBeams: Set<Beam> = setOf(Beam(Point(-1, 0), E))
        val beamCache: MutableSet<Set<Beam>> = mutableSetOf()
        val energized: MutableSet<Point> = mutableSetOf()

        do {
            activeBeams = activeBeams.flatMap { beam -> beam.move(input.getChar(beam.p)) }.toSet()
        }
    }

    private fun List<String>.getChar(p: Point): Char? {
        return this.getOrNull(p.y)?.getOrNull(p.x)
    }

    private fun compute2(input: List<String>): Int {
        TODO()
    }

    private data class Beam(
        val p: Point,
        val direction: Direction
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
                    N -> TODO()
                    E -> TODO()
                    S -> TODO()
                    W -> TODO()
                }
                '\\' -> TODO()
                '-' -> TODO()
                '|' -> TODO()
                '.' -> setOf(move(direction))
                else -> emptySet()
            }

            return when (direction) {
                N -> when (char) {
                    '/' -> listOf(move(E))
                    '\\' -> listOf(move(W))
                    '-' -> listOf(move(W), move(E))
                    else -> listOf(move(N))
                }

                E -> when (char) {
                    '/' -> listOf(move(N))
                    '\\' -> listOf(move(S))
                    '|' -> listOf(move(N), move(S))
                    else -> listOf(move(E))
                }

                S -> when (char) {
                    '/' -> listOf(move(W))
                    '\\' -> listOf(move(E))
                    '-' -> listOf(move(W), move(E))
                    else -> listOf(move(S))
                }

                W -> when (char) {
                    '/' -> listOf(move(S))
                    '\\' -> listOf(move(N))
                    '|' -> listOf(move(N), move(S))
                    else -> listOf(move(W))
                }
            }.toSet()
        }
    }

    enum class Direction {
        N, E, S, W
    }
}
