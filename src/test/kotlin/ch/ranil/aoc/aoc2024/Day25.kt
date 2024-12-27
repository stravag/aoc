package ch.ranil.aoc.aoc2024

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import ch.ranil.aoc.common.allPoints
import ch.ranil.aoc.common.charFor
import ch.ranil.aoc.common.types.Point
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day25 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(3, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(3021, compute1(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val (locks, keys) = parse(input)

        return locks
            .flatMap { lock -> keys.map { key -> lock.matches(key) } }
            .count { it }
    }

    private fun parse(input: List<String>): Pair<Set<Lock>, Set<Key>> {
        fun <T> build(input: List<String>, constructor: (Set<Point>) -> T): T {
            val data = input
                .allPoints()
                .filter { input.charFor(it) == '#' }
                .toSet()
            return constructor(data)
        }

        val locks = mutableSetOf<Lock>()
        val keys = mutableSetOf<Key>()

        var offset = 0
        while (offset < input.size) {
            val next = input
                .drop(offset)
                .takeWhile { it.isNotBlank() }

            when (next.first()) {
                "#####" -> build(next, ::Lock).also { locks.add(it) }
                "....." -> build(next, ::Key).also { keys.add(it) }
            }

            offset += next.size + 1
        }

        return locks to keys
    }

    private data class Lock(val data: Set<Point>) {
        fun matches(key: Key): Boolean {
            val hasOverlaps = this.data.intersect(key.data).isNotEmpty()
            return !hasOverlaps
        }
    }

    private data class Key(val data: Set<Point>)
}
