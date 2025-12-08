package ch.ranil.aoc.aoc2025

import ch.ranil.aoc.common.AbstractDay
import ch.ranil.aoc.common.Debug
import org.junit.jupiter.api.Test
import java.util.PriorityQueue
import kotlin.math.sqrt
import kotlin.test.assertEquals

class Day08 : AbstractDay() {

    @Test
    fun part1Test() {
        Debug.enable()
        assertEquals(40, compute1(testInput, 10))
    }

    @Test
    fun part1Puzzle() {
        assertEquals(131150, compute1(puzzleInput, 1000))
    }

    @Test
    fun part2Test() {
        Debug.enable()
        assertEquals(0, compute2(testInput))
    }

    @Test
    fun part2Puzzle() {
        assertEquals(0, compute2(puzzleInput))
    }

    private fun compute1(input: List<String>, numberOfConnections: Int): Long {
        val junctionBoxes = input.map(::toJunctionBox)
        val boxPairs = junctionBoxes.uniquePairs()

        val circuits = junctionBoxes.map { mutableSetOf(it) }.toMutableSet()
        repeat (numberOfConnections) {
            val (b1, b2) = boxPairs.poll()
            val b1Circuit = circuits.single { b1 in it }
            val b2Circuit = circuits.single { b2 in it }
            if (b1Circuit != b2Circuit) {
                circuits.removeIf { b2 in it }
                b1Circuit.addAll(b2Circuit)
            }
        }

        return circuits
            .sortedByDescending { it.size }
            .take(3)
            .map { it.size.toLong() }
            .reduce(Long::times)
    }

    private fun toJunctionBox(row: String): JunctionBox {
        val (x, y, z) = row.split(",").map { it.toInt() }
        return JunctionBox(x, y, z)
    }

    private fun List<JunctionBox>.uniquePairs(): PriorityQueue<BoxPair> {
        val result = PriorityQueue<BoxPair>()
        for (i in indices) {
            for (j in i + 1 until size) {
                result.add(
                    BoxPair(this[i], this[j])
                )
            }
        }
        return result
    }

    private fun compute2(input: List<String>): Long {
        return input.size.toLong()
    }

    private data class JunctionBox(val x: Int, val y: Int, val z: Int) {
        override fun toString(): String = "$x,$y,$z"
    }

    private data class BoxPair(
        val b1: JunctionBox,
        val b2: JunctionBox,
    ) : Comparable<BoxPair> {

        fun distance(
        ): Double {
            val dx = (b2.x - b1.x).toDouble()
            val dy = (b2.y - b1.y).toDouble()
            val dz = (b2.z - b1.z).toDouble()

            return sqrt(dx * dx + dy * dy + dz * dz)
        }

        override fun compareTo(other: BoxPair): Int {
            return distance().compareTo(other.distance())
        }
    }
}
