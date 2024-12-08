package ch.ranil.aoc.aoc2023

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.aoc2023.Day08.Direction.*
import ch.ranil.aoc.common.lcm
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class Day08 : AbstractDay() {

    @Test
    fun part1Test() {
        assertEquals(2, compute1(testInput))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(21883, compute1(puzzleInput))
    }

    @Test
    fun part2Test() {
        assertEquals(6, compute2(test2Input))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(12833235391111, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>): Int {
        val (sequence, map) = parse(input)

        var hops = 0
        var node = "AAA"
        while (node != "ZZZ") {
            node = when (sequence.next()) {
                LEFT -> map.getValue(node).first
                RIGHT -> map.getValue(node).second
            }
            hops++
        }
        return hops
    }

    private fun compute2(input: List<String>): Long {
        val (sequence, map) = parse(input)

        val startNodes = map.keys.filter { it.endsWith("A") }
        val hops = startNodes.map { node ->
            sequence.reset()
            var hops = 0L
            var n = node
            while (!n.endsWith("Z")) {
                n = when (sequence.next()) {
                    LEFT -> map.getValue(n).first
                    RIGHT -> map.getValue(n).second
                }
                hops++
            }
            hops
        }

        return hops.reduceRight { a, b -> lcm(a, b) }
    }

    enum class Direction {
        LEFT, RIGHT;

        companion object {
            fun of(c: Char): Direction = when (c) {
                'R' -> RIGHT
                'L' -> LEFT
                else -> throw IllegalArgumentException()
            }
        }
    }

    fun parse(input: List<String>): Pair<Sequence, Map<String, Pair<String, String>>> {
        val s = Sequence(input.first())

        val m = input
            .drop(2)
            .map { it.split(" = ") }
            .associateBy(
                keySelector = { (k, _) -> k },
                valueTransform = { (_, v) ->
                    val vals = Regex("\\((\\w{3}), (\\w{3})\\)").find(v)!!.groupValues
                    vals[1] to vals[2]
                }
            )
        return s to m
    }

    class Sequence(private val s: String) {
        private var i = 0

        fun next(): Direction {
            val d = Direction.of(s[i++ % s.length])
            return d
        }

        fun reset() {
            i = 0
        }
    }

    @Test
    fun sequenceTest() {
        val s = Sequence("LR")
        assertEquals(LEFT, s.next())
        assertEquals(RIGHT, s.next())
        assertEquals(LEFT, s.next())
    }
}
