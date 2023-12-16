package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.AbstractDay
import ch.ranil.aoc.PrintColor
import ch.ranil.aoc.aoc2023.Day16.Direction.*
import ch.ranil.aoc.printColor
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day16 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(46, compute1(testInput))
    }

    @Test
    fun part1TestManual() {
        assertEquals(
            3,
            compute1(
                """
            .\
            ..
                """.trimIndent().lines(),
            ),
        )
    }

    @Test
    fun part1Puzzle() {
        assertEquals(6361, compute1(puzzleInput))
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
        val startPoint = Point(0, 0)
        var activeBeams: Set<Beam> = setOf(Beam(startPoint, E))
        val beamCache: MutableSet<Set<Beam>> = mutableSetOf(activeBeams)
        val energized: MutableSet<Point> = mutableSetOf(startPoint)

        do {
            activeBeams = activeBeams
                .flatMap { beam -> beam.move(input.getChar(beam.p)) }
                .filter { it.p.containedIn(input) }
                .toSet()
            energized.addAll(activeBeams.map { it.p })
        } while (beamCache.add(activeBeams))

        printMap(input, energized, activeBeams)

        return energized.size
    }

    private fun List<String>.getChar(p: Point): Char? {
        return this.getOrNull(p.y)?.getOrNull(p.x)
    }

    private fun compute2(input: List<String>): Int {
        TODO()
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

    private fun printMap(input: List<String>, energized: Set<Point>, activeBeams: Set<Beam>) {
        input.forEachIndexed { y, s ->
            s.forEachIndexed { x, c ->
                val point = Point(x, y)
                if (activeBeams.any { it.p == point }) {
                    printColor(PrintColor.YELLOW, c)
                } else if (energized.contains(point)) {
                    printColor(PrintColor.GREEN, c)
                } else {
                    print(c)
                }
            }
            println()
        }
        println()
    }
}
